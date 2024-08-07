package com.vahid.videocalling.video


sealed class VideoCallAction {
    data class OnReportUser(val userId: String) : VideoCallAction()
    data object OnDisconnectClick : VideoCallAction()
    data object JoinCall : VideoCallAction()
}