package com.yichen.community.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimeUtilsTest {

    @Test
    public void getFormatedDiffTime() {
        String diffTimeTest1 = null;
        diffTimeTest1 = TimeUtils.getFormatedDiffTime(0L, 5*1000L);
        Assert.assertEquals("5秒前", diffTimeTest1);

        diffTimeTest1 = TimeUtils.getFormatedDiffTime(0L, 5*60*1000L);
        Assert.assertEquals("5分钟前", diffTimeTest1);

        diffTimeTest1 = TimeUtils.getFormatedDiffTime(0L, 5*60*60*1000L);
        Assert.assertEquals("5小时前", diffTimeTest1);

        diffTimeTest1 = TimeUtils.getFormatedDiffTime(0L, 5*24*60*60*1000L);
        Assert.assertEquals("5天前", diffTimeTest1);

        diffTimeTest1 = TimeUtils.getFormatedDiffTime(0L, 5*30*24*60*60*1000L);
        Assert.assertEquals("5个月前", diffTimeTest1);

        diffTimeTest1 = TimeUtils.getFormatedDiffTime(0L, 5*365*24*60*60*1000L);
        Assert.assertEquals("5年前", diffTimeTest1);
    }
}