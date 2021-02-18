package io.github.jasonkayzk.playersyncbackend.consts;

public enum PlayState {
    /**
     * 首次同步
     */
    FIRST,
    /**
     * 暂停事件
     */
    PAUSE,
    /**
     * 播放事件
     */
    PLAY,
    /**
     * 进度条拖动完成事件
     */
    SEEKED
}
