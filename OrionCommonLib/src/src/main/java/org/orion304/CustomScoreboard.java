package src.main.java.org.orion304;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class CustomScoreboard {

	private static final String name = "custom";

	private final Scoreboard scoreboard;
	private final List<String> lines = new ArrayList<>();

	public CustomScoreboard() {
		this(Bukkit.getScoreboardManager().getNewScoreboard());
	}

	public CustomScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
		Objective objective = this.scoreboard.registerNewObjective(name, "dummy");
		objective.setDisplayName("default");
		for (int i = 0; i < ChatColor.values().length; i++) {
			this.lines.add(" ");
			setLine(i, null);
		}
	}

	public void clearLines() {
		for (int i = 0; i < this.lines.size(); i++) {
			String l = this.lines.get(i);
			if (l != null) {
				this.scoreboard.resetScores(l);
				setLine(i, null);
			}
		}
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public String getSidebarDisplayName() {
		return this.scoreboard.getObjective(name).getDisplayName();
	}

	public void hideCustomSidebar() {
		Objective objective = this.scoreboard.getObjective(name);
		if (objective.getDisplaySlot() == DisplaySlot.SIDEBAR) {
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
	}

	public void setLine(int i, String line) {
		String l = this.lines.get(i);
		if (l != null) {
			this.scoreboard.resetScores(l);
		}
		if (line == null) {
			this.lines.set(i, null);
			return;
		}
		ChatColor color = ChatColor.values()[i];
		ChatColor c = null;
		if (line.length() > 1) {
			c = ChatColor.getByChar(line.charAt(1));
		}

		line = color.toString() + ((c == null) ? ChatColor.RESET : "") + line;

		if (line.length() > 16) {
			line = line.substring(0, 16);
		}

		l = line;
		Objective objective = this.scoreboard.getObjective(name);
		Score score = objective.getScore(l);
		score.setScore(i);
		this.lines.set(i, line);
	}

	public void setSidebarDisplayName(String string) {
		Objective objective = this.scoreboard.getObjective(name);
		objective.setDisplayName(string);
	}

	public void showCustomSidebar() {
		Objective objective = this.scoreboard.getObjective(name);
		if (objective.getDisplaySlot() != DisplaySlot.SIDEBAR) {
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
	}

}
