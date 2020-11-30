package com.zh.study;

import com.zh.study.jdk.GamePlay;
import com.zh.study.jdk.Play;
import com.zh.study.jdk.PlayProxy;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Play play = new GamePlay();
        PlayProxy playProxy = new PlayProxy(play);
        Play playGame = (Play) playProxy.getProxyInstance();
        playGame.play();
        playGame.playBall();
        System.out.println(System.getProperty("user.dir"));
    }
}
