package com.mygdx.potatoandtomato.absintflis.recorder;

import com.badlogic.gdx.files.FileHandle;
import com.potatoandtomato.common.Status;

/**
 * Created by SiongLeng on 12/1/2016.
 */
public abstract class RecordListener {

    public abstract void onFinishedRecord(FileHandle resultFile, Status status);

}