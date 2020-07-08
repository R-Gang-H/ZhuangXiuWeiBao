package com.zhuangxiuweibao.app.bean;

import android.media.SoundPool;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Sound {

    private SoundPool soundPool1;
    private int sampleId;
    private int status;

}
