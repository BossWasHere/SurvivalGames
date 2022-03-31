/*
 * BackwardsNode's Survival Games, a Minecraft Bukkit custom gamemode
 * Copyright (C) 2019-2022 BackwardsNode/BossWasHere
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.backwardsnode.survivalgames;

import com.backwardsnode.survivalgames.editor.EditorQueries;
import com.backwardsnode.survivalgames.editor.PredicateResult;
import com.backwardsnode.survivalgames.editor.ResolvingPredicate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EditorTest {

	final String str = new String();
	final String doubleStr = "1.0";
	final String intStr = "1";
	
	@Test
	void testQueries() {
		ResolvingPredicate<String, ?> p;
		for (EditorQueries query : EditorQueries.values()) {
			p = query.getPredicate();
			switch (query) {
			case BORDER_DPS:
			case BORDER_START_RADIUS:
				assertAsDouble(p);
				break;
			case GRACE_PERIOD:
			case TIME_TO_SHRINK:
			case WAIT_PERIOD:
				assertAsInteger(p);
				break;
			case MAP_NAME:
			case NEW_ITEMSET_NAME:
			case RENAME_ITEMSET_NAME:
				assertAsString(p);
				break;
			}
		}
	}
	
	void assertAsString(ResolvingPredicate<String, ?> p) {
		PredicateResult<String, ?> r = p.validate(str);
		assertTrue(r.SUCCESSFUL);
		assertNotNull(r.wrapOutput(String.class));
		r = p.validate(doubleStr);
		assertTrue(r.SUCCESSFUL);
		assertNotNull(r.wrapOutput(String.class));
		r = p.validate(intStr);
		assertTrue(r.SUCCESSFUL);
		assertNotNull(r.wrapOutput(String.class));
	}
	
	void assertAsDouble(ResolvingPredicate<String, ?> p) {
		PredicateResult<String, ?> r = p.validate(str);
		assertFalse(r.SUCCESSFUL);
		r = p.validate(doubleStr);
		assertTrue(r.SUCCESSFUL);
		assertNotNull(r.wrapOutput(Double.class));
		r = p.validate(intStr);
		assertTrue(r.SUCCESSFUL);
		assertNotNull(r.wrapOutput(Double.class));
	}
	
	void assertAsInteger(ResolvingPredicate<String, ?> p) {
		PredicateResult<String, ?> r = p.validate(str);
		assertFalse(r.SUCCESSFUL);
		r = p.validate(doubleStr);
		assertFalse(r.SUCCESSFUL);
		r = p.validate(intStr);
		assertTrue(r.SUCCESSFUL);
		assertNotNull(r.wrapOutput(Integer.class));
	}
}
