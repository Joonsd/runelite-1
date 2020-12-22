package net.runelite.client.plugins.oldschoolmusic;

import lombok.SneakyThrows;
import net.runelite.api.Client;
import net.runelite.api.WorldType;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetHiddenChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.sound.midi.*;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;

import static net.runelite.api.widgets.WidgetID.MUSIC_GROUP_ID;

@PluginDescriptor(
        name = "OldSchoolMusic",
        description = "Change OSRS music to older ones"
)

public class OldSchoolMusic extends Plugin
{

    @Inject
    private Client client;

    private boolean loggingIn;

    private static Sequencer sequencer;
    private static Synthesizer synthesizer;

    private final String loginSong = "runescape-scape main";

    private final int volume = 70;
    private int currentVolume;

    private final int fadeInTicks = 70; // 70 * 50ms == something 3400ms
                                        // if maxVolume is 140, then -2 volume every tick
                                        // 70 is -1
                                        // 35 is -1 every other tick
    private CurrentState musicState = CurrentState.Idle;

    private boolean busy;

    private final int PLAYING_MUSIC_PARENT_ID = 15663104;
    private final int PLAYING_MUSIC_ID = 15663110;

    @Override
    protected void startUp()
    {
        loggingIn = true;
        currentVolume = volume;
    }

    @Override
    protected void shutDown()
    {
        if (sequencer != null) {
            sequencer.stop();
            sequencer.close();
            sequencer = null;
        }

        if (synthesizer != null) {
            synthesizer.close();
            synthesizer = null;
        }
        client.setMusicVolume(200);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        switch (event.getGameState())
        {
            case LOGGING_IN:
            case CONNECTION_LOST:
            case HOPPING:
                loggingIn = true;

            case LOGIN_SCREEN:
                if (checkPreviousSong(loginSong) == false)
                    return;
                playOldSong(loginSong, true);

            case LOGIN_SCREEN_AUTHENTICATOR:
            case LOADING:
            case STARTING:
        }
    }

    private int tickCounter = 0;
  private int j = 100;
    private String lastSong;
    private String currentSong;

    @Subscribe
    public void onGameTick(GameTick event)
    {
        String newSong = client.getWidget(239, 6).getText();

        if (checkPreviousSong(newSong) == false)
            return;

        System.out.println("Song changes to: " + newSong);

        String oldSongName = getOldSongName(newSong);
        playOldSong(oldSongName, false);


        if (!loggingIn)
        {
            return;
        }

        loggingIn = false;
    }

    public boolean checkPreviousSong(String newSong) {
        if (currentSong == newSong || newSong == "AUTO" || newSong == "MANUAL") {
            return false;
        }

        lastSong = currentSong;
        currentSong = newSong;

        return true;
    }

        public String getOldSongName(String songName) {
            String prefix = "runescape-";
            String oldSongName = prefix+songName;
            return oldSongName.toLowerCase();
        }

        public void playOldSong(String songName, boolean noFade) {
            try {
                client.setMusicVolume(0);

                String pathToSong = OldSchoolMusic.class.getResource("").getPath() + "runescape_music/"+songName+".mid";
                System.out.println(pathToSong);
                stopFadeOut(noFade);
                play(pathToSong);

            } catch (Exception e) {
                System.out.println("Err" + e.getMessage());
                stopFadeOut(noFade);

                client.setMusicVolume(200);
                System.out.println("Song not found. Playing OSRS music");
            }
        }

        private void play(String song) {
            try {

                if (busy)
                {
                    System.out.println("busybusybusybusy");
                    return;
                }

                busy = true;

                new Thread( new Runnable() {
                    @SneakyThrows
                    public void run()  {
                        try  {

                            System.out.println("\nWaiting.");

                            while(musicState != CurrentState.Idle)
                            {
                                Thread.sleep( 100 );
                            }

                            System.out.println("\nWaiting is over");

                            createSequencer();
                            createSynthesizer();

                            Sequence sequence = MidiSystem.getSequence(new File(song));

                            sequencer.open();
                            sequencer.setSequence(sequence);

                            Thread.sleep( 400 );

                            setVolume(volume);
                            // Start playing
                            sequencer.start();
                            musicState = CurrentState.Playing;
                            busy = false;

                        }
                        catch (Exception err)  {
                            System.out.println("Err" + err.getMessage());
                            stopFadeOut(false);
                            musicState = CurrentState.Idle;
                            busy = false;
                            client.setMusicVolume(200);
                            System.out.println("Song not found. Playing OSRS music");
                        }
                    }
                } ).start();

            } catch (Exception err) {
                System.out.println("Err" + err.getMessage());
            }
        }

        private void stopFadeOut(boolean noFade) {

            try {
                musicState = CurrentState.Stopping;

                if (noFade || sequencer == null || synthesizer == null)
                {
                    if (sequencer != null) {
                        sequencer.stop();
                        sequencer.close();
                    }

                    if (synthesizer != null) {
                        synthesizer.close();
                    }

                    sequencer = null;
                    synthesizer = null;
                    musicState = CurrentState.Idle;

                    return;
                }

                Timer t = new Timer( );
                t.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {

                        if (currentVolume <= 0) {

                            if (sequencer != null) {
                                if (sequencer.isRunning())
                                    sequencer.stop();

                                sequencer.close();
                            }

                            if (synthesizer != null) {
                                synthesizer.close();
                            }

                            sequencer = null;
                            synthesizer = null;

                            musicState = CurrentState.Idle;

                            cancel();
                        }
                        else {
                            if  (setVolume(currentVolume - 1) == false)
                            {
                                sequencer = null;
                                synthesizer = null;

                                musicState = CurrentState.Idle;

                                cancel();
                            }
                        }
                    }
                }, 0,50);

            } catch (Exception err) {
                System.out.println("failed to fade out" + err.getMessage());
            }
        }

        private boolean setVolume(int vlm) {
            try {
                javax.sound.midi.MidiChannel[] channels = synthesizer.getChannels();
                for( int c = 0; channels != null && c < channels.length; c++ )
                {
                    channels[c].controlChange( 7, vlm );
                }

                currentVolume = vlm;
                System.out.print(vlm + " ");

                return true;
            } catch (Exception err) {
                System.out.println("failed to set volume" + err.getMessage());
                return false;
            }

        }

    private void createSequencer() {
        try {
            // Create a sequencer for the sequence
            sequencer = MidiSystem.getSequencer(false);
        } catch (Exception err) {
            System.out.println("Err" + err.getMessage());
        }
    }

    private void createSynthesizer() {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();

            sequencer.getTransmitter().setReceiver(
                    synthesizer.getReceiver() );

        } catch (Exception err) {
            System.out.println("Err" + err.getMessage());
        }
    }

    private void setMUTE(boolean mute) {
        javax.sound.midi.MidiChannel[] channels = synthesizer.getChannels();
        for( int c = 0; channels != null && c < channels.length; c++ )
        {
            channels[c].setMute(mute);
        }
    }

    private void setSequenceMuteState(Sequence sequence, boolean mute) {
        for(int i = 0; i < sequence.getTracks().length ; i++) {
            sequencer.setTrackMute(i, mute);
        }
    }

    /*

            private void PlayFadeIn(String song, boolean noFade) {
            try {

                if (busy)
                {
                    System.out.println("busybusybusybusy");
                    return;
                }

                busy = true;

                new Thread( new Runnable() {
                    @SneakyThrows
                    public void run()  {
                        try  {

                            System.out.println("\nWaiting.");

                            while(musicState != CurrentState.Idle)
                            {
                                Thread.sleep( 100 );
                            }

                            System.out.println("\nWaiting is over");

                            createSequencer();
                            createSynthesizer();

                            Sequence sequence = MidiSystem.getSequence(new File(song));

                            sequencer.open();
                            sequencer.setSequence(sequence);

                            Thread.sleep( 400 );

                            musicState = CurrentState.Playing;

                            if (noFade) {
                                setVolume(volume);
                                // Start playing
                                sequencer.start();
                                busy = false;
                                return;
                            }

                            setSequenceMuteState(sequence, true);

                            Thread.sleep( 200 );

                            sequencer.start();
                            if  (setVolume(0) == false)
                            {
                                sequencer = null;
                                synthesizer = null;

                                musicState = CurrentState.Idle;
                                busy = false;
                                return;
                            }

                            Thread.sleep( 200 );

                            Timer t = new Timer( );
                            t.scheduleAtFixedRate(new TimerTask() {

                                boolean firstTimerIteration = true;

                                @Override
                                public void run() {

                                    if (firstTimerIteration) {
                                        // Start playing
                                        setSequenceMuteState(sequence, false);

                                        firstTimerIteration = false;
                                    }

                                    if (currentVolume >= volume) {
                                        busy = false;
                                        cancel();
                                    }
                                    else {
                                        if  (setVolume(currentVolume + 2) == false)
                                        {
                                            sequencer = null;
                                            synthesizer = null;

                                            musicState = CurrentState.Idle;
                                            busy = false;
                                            cancel();
                                        }
                                    }
                                }
                            }, 0,50);

                        }
                        catch (Exception err)  {
                            System.out.println("Err" + err.getMessage());
                            StopFadeOut(noFade);
                            musicState = CurrentState.Idle;
                            busy = false;
                            client.setMusicVolume(200);
                            System.out.println("Song not found. Playing OSRS music");
                        }
                    }
                } ).start();

            } catch (Exception err) {
                System.out.println("Err" + err.getMessage());
            }
        }

     */
}