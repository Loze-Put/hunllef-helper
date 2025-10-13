package com.hunllefhelper;

import com.hunllefhelper.config.AudioMode;
import com.hunllefhelper.config.HunllefHelperConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;

import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;

@Slf4j
public class AudioPlayer
{
	private HashMap<String, Clip> clips = new HashMap<String, Clip>();
	private float volume = 1f;

	public void tryLoadAudio(HunllefHelperConfig config, String[] clipNames)
	{
        AudioMode audioMode = config.audioMode();

		if (audioMode == AudioMode.Disabled)
		{
			return;
		}

        String audioPack = config.audioMode().getDirName();

        for (String clipName : clipNames)
		{
			tryLoadClip(audioMode, audioPack, clipName);
		}
	}

	public void unloadAudio()
	{
		for (Clip clip : clips.values())
		{
			clip.stop();
			clip.flush();
			clip.close();
		}

		clips.clear();
	}

	public synchronized void playSoundClip(String sound)
	{
		if (clips.containsKey(sound))
		{
			Clip clip = clips.get(sound);
			clip.setFramePosition(0);
			clip.start();
		}
	}

	public void setVolume(int volume)
	{
		float volumeF = volume / 100f;
		volumeF = Math.max(volumeF, 0f);
		volumeF = Math.min(volumeF, 2f);

		if (this.volume != volumeF)
		{
			this.volume = volumeF;

			for (Clip clip : clips.values())
			{
				setClipVolume(clip);
			}
		}
	}

    private boolean tryLoadClip(AudioMode audioMode, String audioPack, String clipName)
    {
        InputStream audioStream = null;
        try
        {
            audioStream = getAudioStream(audioMode, audioPack, clipName);
            if (audioStream == null)
            {
                return false;
            }

            return loadClipFromStream(audioStream, clipName);
        }
        catch (Exception ex)
        {
            log.error("Unable to load sound " + clipName, ex);
            return false;
        }
        finally
        {
            if (audioStream != null)
            {
                try
                {
                    audioStream.close();
                }
                catch (IOException ex)
                {
                    log.warn("Failed to close audio stream for " + clipName, ex);
                }
            }
        }
    }

    private InputStream getAudioStream(AudioMode audioMode, String audioPack, String clipName) throws IOException
    {
        String filename = clipName.substring(clipName.lastIndexOf('/') + 1);

        if (audioMode == AudioMode.Custom)
        {
            String path = "audio/" + filename;
            final File customFile = new File(RuneLite.RUNELITE_DIR, path);
            log.debug("Loading custom audio from: " + customFile.getAbsolutePath());
            return new BufferedInputStream(new FileInputStream(customFile));
        }

        String path = String.format("/audio/%s/%s", audioPack, filename);
        InputStream resourceStream = getClass().getResourceAsStream(path);

        if (resourceStream == null)
        {
            log.error("Audio file not found in resources: " + path);
            return null;
        }

        return new BufferedInputStream(resourceStream);
    }

    private boolean loadClipFromStream(InputStream audioStream, String clipName)
    {
        try (BufferedInputStream bufferedStream =
                     audioStream instanceof BufferedInputStream ?
                             (BufferedInputStream) audioStream :
                             new BufferedInputStream(audioStream);
             AudioInputStream sound = AudioSystem.getAudioInputStream(bufferedStream))
        {
            Clip clip = AudioSystem.getClip();
            clips.put(clipName, clip);
            clip.open(sound);
            setClipVolume(clip);
            return true;
        }
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException | SecurityException ex)
        {
            log.error("Unable to load clip from stream for " + clipName, ex);
            return false;
        }
    }

	private void setClipVolume(Clip clip)
	{
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(20f * (float) Math.log10(volume));
	}
}
