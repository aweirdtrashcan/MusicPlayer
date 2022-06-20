package com.aweirdtrashcan.musicplayer.models

import android.net.Uri

data class Songs(
    val id : Long,
    val displayName : String,
    val album : String,
    val duration : Long,
    val uri : Uri
)
