package net.runelite.client.plugins.oldschoolmusic;

import lombok.SneakyThrows;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VolumeChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.sound.midi.*;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@PluginDescriptor(
        name = "OldSchoolMusic",
        description = "Change OSRS music to older ones"
)

/*
    THIS PLUGIN IS UNFINISHED AND NEEDS TO BE REWRITTEN
    USE WITH CAUTION
 */
public class OldSchoolMusic extends Plugin
{

    @Inject
    private Client client;

    private static Sequencer sequencer;
    private static Synthesizer synthesizer;

    private final String mainLoginSong = "runescape-scape main";
    private final String christmasLoginSong = "runescape-scape santa";
    private final String halloweenLoginSong = "runescape-scape scared";

    private final int PLAYING_MUSIC_PARENT_ID = 15663104;
    private final int PLAYING_MUSIC_ID = 15663110;

    //    private final int volume = 70;
    private int currentVolume;
    private int originalVolume;

    private CurrentState musicState = CurrentState.Idle;

    private boolean busy;

    private String lastSong;
    private String currentSong;
    private Boolean changingVolume = false;
    private Boolean changingVolumeInSubscriber = false;

    private int maxFadeoutIterations = 30;
    private int currentFadeOutIteration = 0;

    @Override
    protected void startUp()
    {
        originalVolume = client.getMusicVolume();
        currentVolume = originalVolume;
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
        client.setMusicVolume(originalVolume);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        System.out.println("State changes to: " + event.getGameState());

        switch (event.getGameState())
        {
            case LOGGING_IN:
            case CONNECTION_LOST:
            case HOPPING:
            case LOGIN_SCREEN:
                if (checkPreviousSong(mainLoginSong) == false ||
                        checkPreviousSong(christmasLoginSong) == false ||
                        checkPreviousSong(halloweenLoginSong) == false)
                {
                    return;
                }

                String loginTheme = getSeasonalLoginTheme();
                setNewSong(loginTheme);
                playOldSong(loginTheme, true);

            case LOGIN_SCREEN_AUTHENTICATOR:
            case LOADING:
            case STARTING:
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        String newSong = client.getWidget(239, 6).getText();

        if (checkPreviousSong(newSong) == false)
            return;

        System.out.println("Song changes to: " + newSong);

        setNewSong(newSong);

        String oldSongName = getOldSongName(newSong);
        playOldSong(oldSongName, false);
    }

    @Subscribe
    public void onVolumeChanged(VolumeChanged volumeChanged)
    {
        System.out.println("onVolumeChanged: " + volumeChanged);

        if (!changingVolumeInSubscriber && !changingVolume && volumeChanged.getType() == VolumeChanged.Type.MUSIC)
        {
            int newVolume = client.getMusicVolume();
            originalVolume = newVolume;

            if (newVolume > 0)
            {
                changingVolumeInSubscriber = true;
                client.setMusicVolume(0);
                setMUTE(false);
                setVolume(originalVolume);
            }
            else {
                setMUTE(true);
                setVolume(0);
            }
        }
        else {
            changingVolumeInSubscriber = false;
        }
    }

    public boolean checkPreviousSong(String newSong) {
        if (currentSong == newSong || newSong == "AUTO" || newSong == "auto" || newSong == "MANUAL") {
            return false;
        }

        return true;
    }

    public void setNewSong(String newSong) {
        lastSong = currentSong;
        currentSong = newSong;
    }

    public String getSeasonalLoginTheme() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getMonth() == Month.DECEMBER && (now.getDayOfMonth() >= 9 && now.getDayOfMonth() <= 31))
        {
            return christmasLoginSong;
        }
        else if ((now.getMonth() == Month.OCTOBER && (now.getDayOfMonth() >= 21)) ||
                 (now.getMonth() == Month.NOVEMBER && (now.getDayOfMonth() <= 11)))
        {
            return halloweenLoginSong;
        }
        else
        {
            return mainLoginSong;
        }
    }

        public String getOldSongName(String songName) {

            if (songName.equalsIgnoreCase("Goblin Village"))
            {
                songName = "gnome";
            }
            else if (songName.equalsIgnoreCase("Dwarf Theme"))
            {
                songName = "gnome theme";
            }

            String prefix = "runescape-";
            String oldSongName = prefix+songName;
            return oldSongName.toLowerCase();
        }

        public void playOldSong(String songName, boolean noFade) {
            try {
                changingVolume = true;

                currentVolume = originalVolume;
                client.setMusicVolume(0);

                changingVolume = false;

                String pathToSong = OldSchoolMusic.class.getResource("").getPath() + "runescape_music/"+songName+".mid";
                InputStream midFile = OldSchoolMusic.class.getResourceAsStream("runescape_music/"+songName+".mid");
                //InputStream midFile = OldSchoolMusic.class.getResourceAsStream(songName+".mid");

                System.out.println(midFile);

                System.out.println(pathToSong);
                stopFadeOut(noFade);
                play(midFile);

            } catch (Exception e) {
                System.out.println("Err" + e.getMessage());
                stopFadeOut(noFade);

                client.setMusicVolume(originalVolume);
                currentVolume = originalVolume;
                System.out.println("Song " + songName + " not found. Playing OSRS music");
            }
        }

        private void play(InputStream song) {
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

                            Sequence sequence = MidiSystem.getSequence(song);

                            sequencer.open();
                            sequencer.setSequence(sequence);

                            Thread.sleep( 400 );

                            setVolume(originalVolume);
                            // Start playing
                            sequencer.start();
                            musicState = CurrentState.Playing;
                            busy = false;

                        }
                        catch (Exception err)  {
                            err.printStackTrace();
                            System.out.println("Err " + err.getMessage());
                            stopFadeOut(false);
                            musicState = CurrentState.Idle;
                            busy = false;
                            client.setMusicVolume(originalVolume);
                            System.out.println("Song not found. Playing OSRS music");
                        }
                    }
                } ).start();

            } catch (Exception err) {
                err.printStackTrace();
                System.out.println("Err " + err.getMessage());
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

                System.out.println("Current vol " + currentVolume);

                final int startVolume = currentVolume;
                currentFadeOutIteration = 0;

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

                            int volumeDown = 0;
                            currentFadeOutIteration++;

                            if (startVolume < maxFadeoutIterations)
                            {
                                if ((maxFadeoutIterations - currentFadeOutIteration) > startVolume)
                                {
                                    volumeDown = 0;
                                }
                                else {
                                    volumeDown = 1;
                                }
                            }
                            else {
                                if (startVolume % maxFadeoutIterations == 0)
                                {
                                    volumeDown = startVolume / maxFadeoutIterations;
                                }
                                else {
                                    volumeDown = startVolume / maxFadeoutIterations;

                                    int remainder = startVolume % maxFadeoutIterations;

                                    if (currentFadeOutIteration <= remainder)
                                    {
                                        volumeDown++;
                                    }
                                }
                            }

                            if  (setVolume(currentVolume - volumeDown) == false)
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

                if (synthesizer != null)
                {
                    javax.sound.midi.MidiChannel[] channels = synthesizer.getChannels();
                    for( int c = 0; channels != null && c < channels.length; c++ )
                    {
                        channels[c].controlChange( 7, vlm );
                    }
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