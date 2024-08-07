package com.vahid.videocalling.video

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.getstream.video.android.compose.permission.rememberCallPermissionsState
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.actions.DefaultOnCallActionHandler
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.core.call.state.LeaveCall

@Composable
fun VideoCallScreen(
    state: VideoCallState,
    onAction: (VideoCallAction) -> Unit
) {
    when {
        state.error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        state.callState == CallState.JOINING -> {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(text = "Joining...")
            }
        }

        else -> {
            val basePermissions = listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
            )
            val bluetoothConnectPermission = if (Build.VERSION.SDK_INT >= 31) {
                listOf(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                emptyList()
            }
            val notificationPermission = if (Build.VERSION.SDK_INT >= 33) {
                listOf(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                emptyList()
            }
            val context = LocalContext.current
            var mDisplayMenu by remember { mutableStateOf(false) }
            CallContent(
                call = state.call,
                modifier = Modifier
                    .fillMaxSize(),
                videoRenderer = { modifier, call, participant, style ->
                    ParticipantVideo(
                        modifier = modifier,
                        call = call,
                        participant = participant,
                        style = style,
                        actionsContent = { actions, call, participant ->
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .background(
                                        Color.Black.copy(alpha = 0.5f),
                                        RoundedCornerShape(16.dp)
                                    )
                            ) {
                                IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                                    Icon(Icons.Default.MoreVert, "", tint = Color.White)
                                }

                                DropdownMenu(
                                    modifier = Modifier.background(Color.Black),
                                    expanded = mDisplayMenu,
                                    onDismissRequest = { mDisplayMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Report User", color = Color.White) },
                                        onClick = {
                                            onAction(
                                                VideoCallAction.OnReportUser(
                                                    participant.userNameOrId.value
                                                )
                                            )
                                        })
                                }
                            }
                        },
                    )
                },
                permissions = rememberCallPermissionsState(
                    call = state.call,
                    permissions = basePermissions + bluetoothConnectPermission + notificationPermission,
                    onPermissionsResult = { permissions ->
                        if (permissions.values.contains(false)) {
                            Toast.makeText(
                                context,
                                "Please grant all permissions to use this app.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            onAction(VideoCallAction.JoinCall)
                        }
                    }
                ),
                onCallAction = { action ->
                    if (action == LeaveCall) {
                        onAction(VideoCallAction.OnDisconnectClick)
                    }

                    DefaultOnCallActionHandler.onCallAction(state.call, action)
                },
                onBackPressed = {
                    onAction(VideoCallAction.OnDisconnectClick)
                }
            )
        }
    }
}