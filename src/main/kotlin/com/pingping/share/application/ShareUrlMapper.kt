package com.pingping.share.application

import com.pingping.share.domain.ShareUrl
import com.pingping.share.dto.request.ShareUrlRequest
import com.pingping.share.dto.response.ShareUrlResponse
import org.springframework.stereotype.Component

@Component
class ShareUrlMapper {

    // Request DTO -> Entity 변환
    fun toEntity(request: ShareUrlRequest, url: String): ShareUrl {
        return ShareUrl(
            url = url,
            eventName = request.eventName,
            neighborhood = request.neighborhood
        )
    }

    // Entity -> Response DTO 변환
    fun toResponse(entity: ShareUrl): ShareUrlResponse {
        return ShareUrlResponse(
            id = entity.id,
            shareUrl = entity.url,
            eventName = entity.eventName,
            neighborhood = entity.neighborhood
        )
    }
}