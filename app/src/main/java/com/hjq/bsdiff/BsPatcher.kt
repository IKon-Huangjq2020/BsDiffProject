package com.hjq.bsdiff

/**
 *
 * @Description:     类作用描述
 * @Author:         hjq
 * @CreateDate:     2021/1/13 9:33
 *
 */
object BsPatcher {

    init {
        System.loadLibrary("bspatcher")
    }


    /**
     * 合成安装包
     *
     * @param oldApk 旧版本安装包，如1.1.1版本
     * @param patch  差分包，Patch文件
     * @param output 合成后新版本apk的输出路径
     */
    external fun bsPatch(
        oldApk: String,
        patch: String,
        output: String
    )

}