package com.hunllefhelper;

import com.hunllefhelper.config.AudioMode;
import com.hunllefhelper.config.HunllefHelperConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.audio.AudioPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

@Singleton
@Slf4j
public class AudioManager
{
    @Inject
    private AudioPlayer audioPlayer;

	private final HashMap<String, byte[]> streams = new HashMap<>();
    private float gain = 1f;

	public void loadAudio(HunllefHelperConfig config, String[] clipNames)
    {
        AudioMode audioMode = config.audioMode();

		if (audioMode == AudioMode.Disabled)
		{
			return;
		}

        String audioPack = config.audioMode().getDirName();

        for (String clipName : clipNames)
		{
			loadClip(audioMode, audioPack, clipName);
		}
	}

	public void unloadAudio()
	{
		streams.clear();
	}

	public synchronized void playSoundClip(String clipName)
	{
		if (streams.containsKey(clipName))
		{
            byte[] audioBytes = streams.get(clipName);
            InputStream stream = new ByteArrayInputStream(audioBytes);

            try {
                audioPlayer.play(stream, gain);
            }
            catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex)
            {
                log.error("Unable to play stream for {}", clipName, ex);
            }
		}
	}

	public void setVolume(int volume)
	{
		float volumeF = volume / 100f;
		volumeF = Math.max(volumeF, 0f);
		volumeF = Math.min(volumeF, 2f);
        gain = (20f * (float) Math.log10(volumeF));
	}

    private void loadClip(AudioMode audioMode, String audioPack, String clipName)
    {
        try(InputStream audioStream = getAudioStream(audioMode, audioPack, clipName);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream())
        {
            if (audioStream != null)
            {
                audioStream.transferTo(buffer);
                streams.put(clipName, buffer.toByteArray());
            }
        }
        catch (Exception ex)
        {
            log.error("Unable to load sound {}", clipName, ex);
        }
    }

    private InputStream getAudioStream(AudioMode audioMode, String audioPack, String clipName) throws IOException
    {
        if (audioMode == AudioMode.Custom)
        {
            String path = "audio/" + clipName;
            final File customFile = new File(RuneLite.RUNELITE_DIR, path);
            log.debug("Loading custom audio from: {}", customFile.getAbsolutePath());
            return new BufferedInputStream(Files.newInputStream(customFile.toPath()));
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
