package com.vipa.medlabel.service.task;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.vipa.medlabel.config.redisconfig.RedisCache;
import com.vipa.medlabel.dto.middto.task.TaskProcessDto;
import com.vipa.medlabel.model.Image;
import com.vipa.medlabel.repository.ImageRepository;
import com.vipa.medlabel.dto.middto.task.CreateImageConvertTaskDto;
import com.vipa.medlabel.dto.middto.task.ImageConvertTaskCallbackDto;

import io.jsonwebtoken.io.SerializationException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final RabbitTemplate rabbitTemplate;

    private final RedisCache redisCache;

    private final ImageRepository imageRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private static final String IMAGE_CONVERT_TASK_PROGRESS_CACHE_KEY = "medlabel_image_convert_task_progress";
    private static final String IMAGE_CONVERT_TASK_SUCCESS_CACHE_KEY = "medlabel_image_convert_task_success";
    private static final String IMAGE_CONVERT_TASK_FAILED_CACHE_KEY = "medlabel_image_convert_task_failed";

    private static final String TASK_EXCHANGE = "task_exchange";
    private static final String IMAGE_CONVERT_ROUTINGKEY = "image_convert";
    private static final String TASK_PROGRESS_TOPIC = "/topic/task_progress/";

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${computation.server.url}")
    private String computationServerUrl;

    public String submitImageConvertTask(
            @Valid CreateImageConvertTaskDto createImageConvertTaskDto)
            throws AmqpException, RedisConnectionFailureException, SerializationException {
        // 创建任务ID
        String taskId = String.format("%s_%s_%d", applicationName, IMAGE_CONVERT_ROUTINGKEY,
                createImageConvertTaskDto.getImageId());

        createImageConvertTaskDto.setTaskId(taskId);
        // 创建图像转换任务消息
        rabbitTemplate.convertAndSend(TASK_EXCHANGE, IMAGE_CONVERT_ROUTINGKEY,
                createImageConvertTaskDto);

        // 创建任务进度缓存
        redisCache.setCacheMapValue(IMAGE_CONVERT_TASK_PROGRESS_CACHE_KEY, taskId,
                new TaskProcessDto(createImageConvertTaskDto.getImageId()));

        return taskId;
    }

    @Transactional
    @Retryable(retryFor = {
            ObjectOptimisticLockingFailureException.class }, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void imageConvertTaskFinishCallback(@Valid ImageConvertTaskCallbackDto imageConvertTaskCallbackDto) {
        boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
        System.out.println("Transaction active: " + isActive);
        String taskId = imageConvertTaskCallbackDto.getTaskId();

        TaskProcessDto taskProcessDto = redisCache.<TaskProcessDto>getCacheMapValue(
                IMAGE_CONVERT_TASK_PROGRESS_CACHE_KEY, taskId,
                TaskProcessDto.class);

        Integer newStatus = imageConvertTaskCallbackDto.getStatus();
        Float newProgress = imageConvertTaskCallbackDto.getProgress();

        Image image = imageRepository.findById(imageConvertTaskCallbackDto.getImageId())
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        // 保存任务状态
        if (!image.getStatus().equals(newStatus)) {
            image.setStatus(newStatus);
            imageRepository.save(image);
        }

        if (newStatus == 0 || newStatus == 1) {
            taskProcessDto.updateProgress(newStatus, newProgress);
            // 保存任务进度缓存
            redisCache.setCacheMapValue(IMAGE_CONVERT_TASK_PROGRESS_CACHE_KEY, taskId, taskProcessDto);
        } else if (newStatus == 3) {
            taskProcessDto.updateResult(newStatus, newProgress, imageConvertTaskCallbackDto.getResult());
            log.error("imageConvertTaskFinishCallback: task failed, taskId: {}, message {}", taskId,
                    taskProcessDto.getResult());
            // 更新任务进度缓存
            redisCache.delCacheMapValue(IMAGE_CONVERT_TASK_PROGRESS_CACHE_KEY, taskId);
            redisCache.setCacheMapValue(IMAGE_CONVERT_TASK_FAILED_CACHE_KEY, taskId, taskProcessDto);
        } else if (newStatus == 2) {
            taskProcessDto.updateResult(newStatus, newProgress, imageConvertTaskCallbackDto.getResult());

            // 更新任务进度缓存
            redisCache.delCacheMapValue(IMAGE_CONVERT_TASK_PROGRESS_CACHE_KEY, taskId);
            redisCache.setCacheMapValue(IMAGE_CONVERT_TASK_SUCCESS_CACHE_KEY, taskId, taskProcessDto);
        }

        // // 主动推送任务进度
        // sendTaskProgress(taskId, taskProcessDto);
    }

    public void sendTaskProgress(String taskId, TaskProcessDto taskProcessDto) {
        // 使用SimpMessagingTemplate将消息发送到指定的主题
        messagingTemplate.convertAndSend(TASK_PROGRESS_TOPIC + taskId, taskProcessDto);
    }

}
