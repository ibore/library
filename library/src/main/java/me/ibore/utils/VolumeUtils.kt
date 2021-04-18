package me.ibore.utils

import android.content.Context
import android.media.AudioManager
import android.os.Build

/**
 * <pre>
 * author: blankj
 * blog  : http://blankj.com
 * time  : 2020/09/08
 * desc  : utils about volume
</pre> *
 */
object VolumeUtils {
    /**
     * Return the volume.
     *
     * @param streamType The stream type.
     *
     *  * [AudioManager.STREAM_VOICE_CALL]
     *  * [AudioManager.STREAM_SYSTEM]
     *  * [AudioManager.STREAM_RING]
     *  * [AudioManager.STREAM_MUSIC]
     *  * [AudioManager.STREAM_ALARM]
     *  * [AudioManager.STREAM_NOTIFICATION]
     *  * [AudioManager.STREAM_DTMF]
     *  * [AudioManager.STREAM_ACCESSIBILITY]
     *
     * @return the volume
     */
    fun getVolume(streamType: Int): Int {
        val am = Utils.app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.getStreamVolume(streamType)
    }

    /**
     * Sets media volume.<br></br>
     * When setting the value of parameter 'volume' greater than the maximum value of the media volume will not either cause error or throw exception but maximize the media volume.<br></br>
     * Setting the value of volume lower than 0 will minimize the media volume.
     *
     * @param streamType The stream type.
     *
     *  * [AudioManager.STREAM_VOICE_CALL]
     *  * [AudioManager.STREAM_SYSTEM]
     *  * [AudioManager.STREAM_RING]
     *  * [AudioManager.STREAM_MUSIC]
     *  * [AudioManager.STREAM_ALARM]
     *  * [AudioManager.STREAM_NOTIFICATION]
     *  * [AudioManager.STREAM_DTMF]
     *  * [AudioManager.STREAM_ACCESSIBILITY]
     *
     * @param volume     The volume.
     * @param flags      The flags.
     *
     *  * [AudioManager.FLAG_SHOW_UI]
     *  * [AudioManager.FLAG_ALLOW_RINGER_MODES]
     *  * [AudioManager.FLAG_PLAY_SOUND]
     *  * [AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE]
     *  * [AudioManager.FLAG_VIBRATE]
     *
     */
    fun setVolume(streamType: Int, volume: Int, flags: Int) {
        val am = Utils.app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            am.setStreamVolume(streamType, volume, flags)
        } catch (ignore: SecurityException) {
        }
    }

    /**
     * Return the maximum volume.
     *
     * @param streamType The stream type.
     *
     *  * [AudioManager.STREAM_VOICE_CALL]
     *  * [AudioManager.STREAM_SYSTEM]
     *  * [AudioManager.STREAM_RING]
     *  * [AudioManager.STREAM_MUSIC]
     *  * [AudioManager.STREAM_ALARM]
     *  * [AudioManager.STREAM_NOTIFICATION]
     *  * [AudioManager.STREAM_DTMF]
     *  * [AudioManager.STREAM_ACCESSIBILITY]
     *
     * @return the maximum volume
     */
    fun getMaxVolume(streamType: Int): Int {
        val am = Utils.app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.getStreamMaxVolume(streamType)
    }

    /**
     * Return the minimum volume.
     *
     * @param streamType The stream type.
     *
     *  * [AudioManager.STREAM_VOICE_CALL]
     *  * [AudioManager.STREAM_SYSTEM]
     *  * [AudioManager.STREAM_RING]
     *  * [AudioManager.STREAM_MUSIC]
     *  * [AudioManager.STREAM_ALARM]
     *  * [AudioManager.STREAM_NOTIFICATION]
     *  * [AudioManager.STREAM_DTMF]
     *  * [AudioManager.STREAM_ACCESSIBILITY]
     *
     * @return the minimum volume
     */
    fun getMinVolume(streamType: Int): Int {
        val am = Utils.app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            am.getStreamMinVolume(streamType)
        } else 0
    }
}