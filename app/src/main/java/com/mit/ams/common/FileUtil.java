/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.mit.ams.common;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileUtil {
    public static File getSaveFile() {
        File file = new File(Environment.getExternalStorageDirectory()+"/ams/", "pic.jpg");
        return file;
    }
}
