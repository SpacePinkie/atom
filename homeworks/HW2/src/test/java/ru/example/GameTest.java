package ru.example;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class GameTest {

    @Test
    public void TestCows() throws Exception{
        Assert.assertEquals(4, Game.checkCows("swoc", "cows"));
    }


    @Test
    public void TestBulls() throws Exception{
        Assert.assertEquals(2, Game.checkBulls("bu", "bu"));
    }

}
