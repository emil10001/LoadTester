# LoadTester

An Android app to slow your test device down.

## The Problem

I've got a bunch of Android devices at my desk, most of which are several years old. I need them for testing the apps that I work on, both to make sure that they work consistently across OEMs, but also to see how the app performs on older devices. The problem that I kept running into was that the old devices would feel faster than my newer phone, simply because there wasn't as much running on them. 

## The Solution

Add some load to those test devices! I decided to create this app to make the phone do some sort of constant and predictable work in the background. It can do three things, load up the CPU, eat up about 130-140MB of memory (limited by the system here), and make network requests. 

![Screenshot](https://s3.amazonaws.com/ejf3-public/hosted_files/2013-11-26+16.14.38.png)

I also tried to architect and design the thing in a sane way, hopefully it's not too difficult to understand.

## API

In addition to launching and configuring manually, there is an API that allows you to broadcast an intent, with all the configuration options that you get in the UI! Here's how you would launch it:

    Intent launchLoadTest = new Intent();
    launchLoadTest.setAction("com.feigdev.loadtester.api");
    long keepAlive = 20 * 1000;
    launchLoadTest.putExtra("KEEP_ALIVE", keepAlive);
    launchLoadTest.putExtra("CPU_ENABLED", true);
    launchLoadTest.putExtra("RAM_ENABLED", true);
    launchLoadTest.putExtra("NET_ENABLED", true);
    launchLoadTest.putExtra("MODE", LOW");
    sendBroadcast(launchLoadTest);

And, when you're done, you can kill it too:

    Intent launchLoadTest = new Intent();
    launchLoadTest.setAction("com.feigdev.loadtester.api");
    launchLoadTest.putExtra("KILL_NOW", "KILL_NOW");
    sendBroadcast(launchLoadTest);

[Here is a project that launches and kills the LoadTester.](https://github.com/emil10001/LoadTestLauncher)

## Notes

The configurations for load might need to be tweaked. It's all just guess work at this point, and while I was able to put enough load on my Nexus 5 to freeze it up completely, I haven't figured out what the best parameters are for a consistent load on each setting. Feel free to make pull requests with different values.

If you're looking for an apk, [here's one to download](https://s3.amazonaws.com/ejf3-public/hosted_files/LoadTester-debug-unaligned.apk).
