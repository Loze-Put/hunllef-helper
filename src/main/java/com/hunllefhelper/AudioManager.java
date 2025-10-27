package com.hunllefhelper;

import com.hunllefhelper.config.AudioMode;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.audio.AudioPlayer;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

@Singleton
@Slf4j
public class AudioManager
{
    @Inject
    private AudioPlayer audioPlayer;

    private float gain = 0f;

	public synchronized void playSoundClip(String clipName, AudioMode audioMode)
	{
        String audioPack = audioMode.getDirName();

        try (InputStream audioStream = getAudioStream(audioMode, audioPack, clipName))
        {
            if (audioStream != null)
            {
                audioPlayer.play(audioStream, gain);
            }
        }
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex)
        {
            log.error("Unable to play stream for {}", clipName, ex);
        }
	}

	public void setVolume(int volume)
	{
		float volumeF = volume / 100f;
		volumeF = Math.max(volumeF, 0f);
		volumeF = Math.min(volumeF, 2f);
        gain = (20f * (float) Math.log10(volumeF));
	}

    private InputStream getAudioStream(AudioMode audioMode, String audioPack, String clipName) throws IOException
    {
        if (audioMode == AudioMode.Custom)
        {
            String path = "audio/" + clipName;
            final File customFile = new File(RuneLite.RUNELITE_DIR, path);

            try
            {
                return new BufferedInputStream(Files.newInputStream(customFile.toPath()));
            }
            catch (NoSuchFileException ex) {
                log.warn("Cannot play audio file {} because it does not exist", customFile.toPath());
                return null;
            }
        }

        String path = String.format("/audio/%s/%s", audioPack, clipName);
        InputStream resourceStream = getClass().getResourceAsStream(path);

        if (resourceStream == null)
        {
            log.error("Audio file not found in resources: {}", path);
            return null;
        }

        return new BufferedInputStream(resourceStream);
    }
}
