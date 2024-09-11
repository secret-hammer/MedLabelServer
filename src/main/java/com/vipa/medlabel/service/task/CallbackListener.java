package com.vipa.medlabel.service.task;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.vipa.medlabel.dto.middto.task.ImageConvertTaskCallbackDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CallbackListener {
    private final TaskService taskService;

    @RabbitListener(queues = "image_convert_task_finish_callback_queue")
    public void imageConvertTaskFinishCallback(@Valid ImageConvertTaskCallbackDto imageConvertTaskCallbackDto) {
        taskService.imageConvertTaskFinishCallback(imageConvertTaskCallbackDto);
    }

}
