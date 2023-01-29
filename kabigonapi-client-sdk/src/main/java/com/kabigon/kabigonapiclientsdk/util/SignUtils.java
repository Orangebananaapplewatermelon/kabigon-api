package com.kabigon.kabigonapiclientsdk.util;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * @author kabigon
 * @version 2023/1/23/23:58
 */
public class SignUtils {

    public static String getSign(String body, String secretKey) {
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secretKey;
        // 5393554e94bf0eb6436f240a4fd71282
        return md5.digestHex(content);
    }

}
