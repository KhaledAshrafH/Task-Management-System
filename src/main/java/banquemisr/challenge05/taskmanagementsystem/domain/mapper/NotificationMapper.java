package banquemisr.challenge05.taskmanagementsystem.domain.mapper;


import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.NotificationCreationDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.NotificationResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponseDTO toResponseDTO(Notification notification);
    List<NotificationResponseDTO> toResponseDTOs(List<Notification> notifications);

    Notification toEntity(NotificationCreationDTO notificationCreationDTO);

}
