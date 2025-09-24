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
		if (config.audioMode() == AudioMode.Disabled)
		{
			return;
		}

		for (String clipName : clipNames)
		{
			tryLoadClip(config.audioMode(), clipName);
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

	private boolean tryLoadClip(AudioMode audioMode, String clipName)
	{
		if (audioMode == AudioMode.Custom)
		{
			final File customFile = new File(RuneLite.RUNELITE_DIR, clipName);

			try (
				InputStream fileStream = new BufferedInputStream(new FileInputStream(customFile));
				AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream))
			{
				Clip clip = AudioSystem.getClip();
				clips.put(clipName, clip);
				clip.open(sound);
				setClipVolume(clip);
				return true;
			}
			catch (UnsupportedAudioFileException | IOException | LineUnavailableException | SecurityException ex)
			{
				log.error("Unable to load sound " + clipName, ex);
			}
		}

        if (audioMode == AudioMode.Soft_ASMR)
        {
            String resourcePath = clipName.replace("/audio/default/", "/audio/asmr/");

            InputStream resourceStream = getClass().getResourceAsStream(resourcePath);
            if (resourceStream == null)
            {
                log.error("Audio file not found in resources: " + resourcePath);
                return false;
            }

            try (BufferedInputStream bufferedStream = new BufferedInputStream(resourceStream);
                 AudioInputStream sound = AudioSystem.getAudioInputStream(bufferedStream))
            {
                Clip clip = AudioSystem.getClip();
                clips.put(clipName, clip);
                clip.open(sound);
                setClipVolume(clip);
                return true;
            }
            catch (Exception ex)
            {
                log.error("Unable to load sound from resources " + resourcePath, ex);
            }
        }

        try (
			InputStream audioSource = getClass().getResourceAsStream(clipName);
			BufferedInputStream bufferedStream = new BufferedInputStream(audioSource);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedStream))
		{
			Clip clip = AudioSystem.getClip();
			clips.put(clipName, clip);
			clip.open(audioInputStream);
			setClipVolume(clip);
			return true;
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException | SecurityException ex)
		{
			log.error("Unable to load sound " + clipName, ex);
		}

		return false;
	}

	private void setClipVolume(Clip clip)
	{
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(20f * (float) Math.log10(volume));
	}
}
