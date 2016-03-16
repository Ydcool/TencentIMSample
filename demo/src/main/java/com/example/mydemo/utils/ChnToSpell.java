
package com.example.mydemo.utils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Hashtable;

import android.content.Context;

public class ChnToSpell {
    private static final int LENGTH_BUFFER = 1024 * 2;

    public static final String PyMusicCode[] =
    {
            "?",
            "a", "ai", "an", "ang", "ao", "ba", "bai", "ban", "bang", "bao",
            "bei", "ben", "beng", "bi", "bian", "biao", "bie", "bin", "bing", "bo",
            "bu", "ca", "cai", "can", "cang", "cao", "ce", "ceng", "cha", "chai",
            "chan", "chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu",
            "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu",
            "cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao", "de",
            "deng", "di", "dian", "diao", "die", "ding", "diu", "dong", "dou", "du",
            "duan", "dui", "dun", "duo", "e", "en", "er", "fa", "fan", "fang",
            "fei", "fen", "feng", "fu", "fou", "ga", "gai", "gan", "gang", "gao",
            "ge", "ji", "gen", "geng", "gong", "gou", "gu", "gua", "guai", "guan",/* 100 */
            "guang", "gui", "gun", "guo", "ha", "hai", "han", "hang", "hao", "he",
            "hei", "hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang",
            "hui", "hun", "huo", "jia", "jian", "jiang", "qiao", "jiao", "jie", "jin",
            "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan",
            "kang", "kao", "ke", "ken", "keng", "kong", "kou", "ku", "kua", "kuai",
            "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan", "lang", "lao",
            "le", "lei", "leng", "li", "lia", "lian", "liang", "liao", "lie", "lin",
            "ling", "liu", "long", "lou", "lu", "luan", "lue", "lun", "luo", "ma",
            "mai", "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian",
            "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai",/* 200 */
            "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang",
            "niao", "nie", "nin", "ning", "niu", "nong", "nu", "nuan", "nue", "yao",
            "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen",
            "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pou", "pu",
            "qi", "qia", "qian", "qiang", "qie", "qin", "qing", "qiong", "qiu", "qu",
            "quan", "que", "qun", "ran", "rang", "rao", "re", "ren", "reng", "ri",
            "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai", "san",
            "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan", "shang", "shao",
            "she", "shen", "sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang",
            "shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui", "sun",/* 300 */
            "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian",
            "tiao", "tie", "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo",
            "wa", "wai", "wan", "wang", "wei", "wen", "weng", "wo", "wu", "xi",
            "xia", "xian", "xiang", "xiao", "xie", "xin", "xing", "xiong", "xiu", "xu",
            "xuan", "xue", "xun", "ya", "yan", "yang", "ye", "yi", "yin", "ying",
            "yo", "yong", "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan",
            "zang", "zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang",
            "zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua", "zhuai",
            "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi", "zong", "zou", "zu", "zuan",
            "zui", "zun", "zuo", "ei", "m", "n", "dia", "cen", "nou", "jv",/* 400 */
            "qv", "xv", "lv", "nv"
    };

    private static final String sNumeralsRoman[] = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "50", "100", "500",
            "1000",
    };

    public static final int TRANS_MODE_QUAN_PIN = 1;
    public static final int TRANS_MODE_PINYIN_INITIAL = 2;

    private static final String CHN_DATABASE_NAME = "qq_uni2py.db";

    private static short[] sUnicodeToIndex;

    private static InputStream sIn;

    public static void initChnToSpellDB(Context ctx) {
        if (sUnicodeToIndex != null) {
            return;
        }
        if (ctx != null && sIn == null) {
            try {
                sIn = ctx.getResources().getAssets().open(CHN_DATABASE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void readMap() {
        synchronized (ChnToSpell.class) {
            if (sUnicodeToIndex != null) {
                return;
            }
            final int count = HANZI_LAST - HANZI_FIRST + 1;
            sUnicodeToIndex = new short[count];

            byte[] buffer = new byte[LENGTH_BUFFER];
            ByteBuffer bb = ByteBuffer.wrap(buffer);
            BufferedInputStream bis = null;
            int result = 0, pos = 0;
            try {
                bis = new BufferedInputStream(sIn);
                result = bis.read(buffer, 0, LENGTH_BUFFER) / 2;
                while (result != 0 && pos + result <= count) {
                    bb.position(0);
                    bb.asShortBuffer().get(sUnicodeToIndex, pos, Math.min(result, count - pos));
                    pos += result;
                    result = bis.read(buffer, 0, LENGTH_BUFFER) / 2;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sIn != null) {
                    try {
                        sIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sIn = null;
                }
            }
        }
    }

    private static final int ROMAN_FIRST = 0x2160;
    private static final int ROMAN_LAST = 0x217F;
    private static final int HANZI_LING = 0x3007;
    private static final int HANZI_FIRST = 0x4E00;
    private static final int HANZI_LAST = 0x9FA5;
    private static final int QUANJIAO_FIRST = 0xff01;
    private static final int QUANJIAO_LAST = 0xff5E;

    private static final int BANJIAO_FIRST = 0x21;

    private static final int QUANJIAO_BANJIAN_OFFSET = QUANJIAO_FIRST - BANJIAO_FIRST;

    private static final int[] sCodeRange = {
            0, 'A' - 1, 'Z' + 1, 'a' - 1, 'z' + 1, ROMAN_FIRST - 1, ROMAN_LAST + 1, HANZI_LING,
            HANZI_FIRST - 1, HANZI_LAST + 1,
            QUANJIAO_FIRST - 1, QUANJIAO_LAST + 1, Integer.MAX_VALUE
    };

    private static final int RANGE_LING = 5;
    private static final int RANGE_UPPER = -3;
    private static final int RANGE_LOWER = -5;
    private static final int RANGE_ROMAN = -7;
    private static final int RANGE_HANZI = -10;
    private static final int RANGE_QUANJIAO = -12;

    private static Hashtable<String, String> sQuanpinCache = new Hashtable<String, String>(128);
    private static Hashtable<String, String> sInitCache = new Hashtable<String, String>(128);

    public static String MakeSpellCode(String text, int mode) {
        if (text == null || text.length() == 0) {
            return "";
        }
        if (mode == TRANS_MODE_QUAN_PIN) {
            String spellValue = sQuanpinCache.get(text);
            if (spellValue != null) {
                return spellValue;
            }
        } else if (mode == TRANS_MODE_PINYIN_INITIAL) {
            String spellValue = sInitCache.get(text);
            if (spellValue != null) {
                return spellValue;
            }
        }
        if (sUnicodeToIndex == null) {
            readMap();
        }
        StringBuilder result = new StringBuilder();
        final int length = text.length();
        int begin = 0;

        //特殊处理首多音字
        char c = text.charAt(0);
        if (c == '单') {
            if (mode == TRANS_MODE_QUAN_PIN) {
                result.append("shan");
            } else if (mode == TRANS_MODE_PINYIN_INITIAL) {
                result.append('s');
            }
            result.append("shan");
            begin = 1;
        } else if (c == '仇') {
            if (mode == TRANS_MODE_QUAN_PIN) {
                result.append("qiu");
            } else if (mode == TRANS_MODE_PINYIN_INITIAL) {
                result.append('1');
            }
            begin = 1;
        } else if (c == '曾') {
            if (mode == TRANS_MODE_QUAN_PIN) {
                result.append("zeng");
            } else if (mode == TRANS_MODE_PINYIN_INITIAL) {
                result.append('z');
            }
            begin = 1;
        } else if (c == '万') {
            if (text.length() > 1) {
                c = text.charAt(1);
                if (c == '俟') {
                    if (mode == TRANS_MODE_QUAN_PIN) {
                        result.append("moqi");
                    } else if (mode == TRANS_MODE_PINYIN_INITIAL) {
                        result.append("mq");
                    }
                    begin = 2;
                }
            }
        }

        for (int i = begin; i < length; ++i) {
            c = text.charAt(i);
            switch (Arrays.binarySearch(sCodeRange, c)) {
                case RANGE_ROMAN:
                    int romanIndex = c - ROMAN_FIRST;
                    if (romanIndex >= sNumeralsRoman.length) {
                        romanIndex -= sNumeralsRoman.length;
                    }
                    result.append(sNumeralsRoman[romanIndex]);
                    break;
                case RANGE_LING:
                    if (mode == TRANS_MODE_QUAN_PIN) {
                        result.append("ling");
                    } else {
                        result.append('l');
                    }
                    break;
                case RANGE_UPPER:
                    result.append(Character.toLowerCase(c));
                    break;
                case RANGE_LOWER:
                    result.append(c);
                    break;
                case RANGE_HANZI:
                    if (mode == TRANS_MODE_QUAN_PIN) {
                        result.append(PyMusicCode[sUnicodeToIndex[c - HANZI_FIRST]]);
                    } else {
                        result.append(PyMusicCode[sUnicodeToIndex[c - HANZI_FIRST]].charAt(0));
                    }
                    break;
                case RANGE_QUANJIAO:
                    result.append(Character.toLowerCase((char) (c - QUANJIAO_BANJIAN_OFFSET)));
                    break;
                default:
                    if (Character.isHighSurrogate(c)) {
                        result.append('?');
                        ++i;
                    } else {
                        result.append(c);
                    }
                    break;
            }
        }
        String retSpell = result.toString();
        if (mode == TRANS_MODE_QUAN_PIN) {
            sQuanpinCache.put(text, retSpell);
        } else if (mode == TRANS_MODE_PINYIN_INITIAL) {
            sInitCache.put(text, retSpell);
        }
        return retSpell;
    }
}
