package org.bff.javampd.player;

import org.bff.javampd.audioinfo.MPDAudioInfo;
import org.bff.javampd.song.MPDSong;

import java.util.Optional;

/**
 * @author bill
 */
public interface Player {

    /**
     * The status of the player.
     */
    enum Status {

        STATUS_STOPPED("stop"),
        STATUS_PLAYING("play"),
        STATUS_PAUSED("pause");

        private String prefix;

        Status(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    /**
     * Returns the current song either playing or queued for playing.
     *
     * @return the current song
     */
    Optional<MPDSong> getCurrentSong();

    /**
     * Adds a {@link PlayerChangeListener} to this object to receive
     * {@link PlayerChangeEvent}s.
     *
     * @param pcl the PlayerChangeListener to add
     */
    void addPlayerChangeListener(PlayerChangeListener pcl);

    /**
     * Removes a {@link PlayerChangeListener} from this object.
     *
     * @param pcl the PlayerChangeListener to remove
     */
    void removePlayerChangedListener(PlayerChangeListener pcl);

    /**
     * Adds a {@link VolumeChangeListener} to this object to receive
     * {@link VolumeChangeEvent}s.
     *
     * @param vcl the VolumeChangeListener to add
     */
    void addVolumeChangeListener(VolumeChangeListener vcl);

    /**
     * Removes a {@link VolumeChangeListener} from this object.
     *
     * @param vcl the VolumeChangeListener to remove
     */
    void removeVolumeChangedListener(VolumeChangeListener vcl);

    /**
     * Starts the player.
     */
    void play();

    /**
     * Starts the player with the specified {@link MPDSong}.
     *
     * @param song the song to start the player with
     */
    void playSong(MPDSong song);

    /**
     * Seeks to the desired location in the current song.  If the location is larger
     * than the length of the song or is less than 0 then the parameter is ignored.
     *
     * @param secs the location to seek to
     */
    void seek(long secs);

    /**
     * Seeks to the desired location in the specified song.  If the location is larger
     * than the length of the song or is less than 0 then the parameter is ignored.
     *
     * @param song the song to seek in
     * @param secs the location to seek to
     */
    void seekSong(MPDSong song, long secs);

    /**
     * Stops the player.
     */
    void stop();

    /**
     * Pauses the player.
     */
    void pause();

    /**
     * Plays the next song in the playlist.
     */
    void playNext();

    /**
     * Plays the previous song in the playlist.
     */
    void playPrevious();

    /**
     * Mutes the volume of the player.
     */
    void mute();

    /**
     * Unmutes the volume of the player.
     */
    void unMute();

    /**
     * Returns the instantaneous bitrate of the currently playing song.
     *
     * @return the instantaneous bitrate in kbps
     */
    int getBitrate();

    /**
     * Returns the current volume of the player.
     *
     * @return the volume of the player (0-100)
     */
    int getVolume();

    /**
     * Sets the volume of the player.  The volume is between 0 and 100, any volume less
     * that 0 results in a volume of 0 while any volume greater than 100 results in a
     * volume of 100.
     *
     * @param volume the volume level (0-100)
     */
    void setVolume(int volume);

    /**
     * Returns if the player is repeating.
     *
     * @return is the player repeating
     */
    boolean isRepeat();

    /**
     * Sets the repeating status of the player.
     *
     * @param shouldRepeat should the player repeat the current song
     */
    void setRepeat(boolean shouldRepeat);

    /**
     * Returns if the player is in random play mode.
     *
     * @return true if the player is in random mode false otherwise
     */
    boolean isRandom();

    /**
     * Sets the random status of the player. So the songs will be played in random order
     *
     * @param shouldRandom should the player play in random mode
     */
    void setRandom(boolean shouldRandom);

    /**
     * Plays the playlist in a random order.
     */
    void randomizePlay();

    /**
     * Plays the playlist in order.
     */
    void unRandomizePlay();

    /**
     * Returns the cross fade of the player in seconds.
     *
     * @return the cross fade of the player in seconds
     */
    int getXFade();

    /**
     * Sets the cross fade of the player in seconds.
     *
     * @param xFade the amount of cross fade to set in seconds
     */
    void setXFade(int xFade);

    /**
     * Returns the elapsed time of the current song in seconds.
     *
     * @return the elapsed time of the song in seconds
     */
    long getElapsedTime();

    /**
     * Returns the total time of the current song in seconds.
     *
     * @return the elapsed time of the song in seconds
     */
    long getTotalTime();

    /**
     * Returns the {@link org.bff.javampd.audioinfo.MPDAudioInfo} about the current status of the player.
     * If the status is unknown {@code null} will be returned.  Any individual parameter that is not
     * known will be a -1
     *
     * @return the sample rate
     */
    MPDAudioInfo getAudioDetails();

    /**
     * Returns the current status of the player.
     *
     * @return the status of the player
     */
    Status getStatus();


    /**
     * Set 'consume' state of the player.
     *
     * @param consume should consume be turned on
     */
    void setConsume(boolean consume);

    /**
     * Set 'single' state of the player.
     *
     * @param single should the single mode be turned on
     */
    void setSingle(boolean single);

    /**
     * The threshold at which songs will be overlapped. Like crossfading but doesn’t fade the track volume,
     * just overlaps.
     *
     * @return the threshold at which songs will be overlapped
     */
    Optional<Integer> getMixRampDb();

    /**
     * Sets the threshold at which songs will be overlapped. Like crossfading but doesn’t fade the track volume,
     * just overlaps. The songs need to have MixRamp tags added by an external tool.
     * 0dB is the normalized maximum volume so use negative values
     *
     * @param db the threshold at which songs will be overlapped
     */
    void setMixRampDb(int db);

    /**
     * Additional time subtracted from the overlap calculated by mixrampdb.
     *
     * @return additional time subtracted from the overlap calculated by mixrampdb
     */
    Optional<Integer> getMixRampDelay();


    /**
     * Additional time subtracted from the overlap calculated by mixrampdb. A value of < 0> disables MixRamp
     * overlapping and falls back to crossfading.
     *
     * @param delay additional time subtracted from the overlap calculated by mixrampdb
     */
    void setMixRampDelay(int delay);
}
