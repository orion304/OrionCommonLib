package src.main.java.org.orion304;

import org.bukkit.entity.Player;

public class Countdown implements Runnable, Comparable<Countdown> {

	private int exp;
	private final long duration;
	private long starttime;
	private final Player player;
	private final boolean countUp;

	private int level = Integer.MAX_VALUE;

	public Countdown(long duration, Player player, boolean countUp) {
		this.duration = duration;
		this.starttime = System.currentTimeMillis();
		this.player = player;
		this.countUp = countUp;
		if (countUp) {
			this.level = Integer.MIN_VALUE;
		}
	}

	public void addExp(int exp) {
		this.exp += exp;
	}

	@Override
	public int compareTo(Countdown arg0) {
		long time = System.currentTimeMillis();
		Long thisRemainingTime = this.duration - (time - this.starttime);
		Long otherRemainingTime = arg0.duration - (time - arg0.starttime);
		return thisRemainingTime.compareTo(otherRemainingTime);
	}

	public Countdown copy(Player newPlayer) {
		Countdown countdown = new Countdown(this.duration, newPlayer,
				this.countUp);
		countdown.starttime = this.starttime;
		return countdown;
	}

	public void end() {
		this.player.setTotalExperience(this.exp);
	}

	public boolean isActive() {
		return System.currentTimeMillis() < (this.duration + this.starttime);
	}

	@Override
	public void run() {
		long time = System.currentTimeMillis() - this.starttime;
		int level;

		if (this.countUp) {
			level = (int) (time) / 1000;

			if (level > this.level) {
				this.level = level;
				this.player.setLevel(level);
			}
			this.player.setExp((float) (time) / (float) this.duration);
		} else {
			level = (int) (this.duration - time) / 1000;

			if (level < this.level) {
				this.level = level;
				this.player.setLevel(level);
			}
			this.player.setExp((float) (this.duration - time)
					/ (float) this.duration);
		}

	}

	public void start() {
		this.exp = this.player.getTotalExperience();
	}
}
