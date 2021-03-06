package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;

public class SpellLevel
{

	public static boolean levelForKeyContains(Spell sp,
			List<? extends CDOMList<Spell>> lists, int levelMatch,
			PlayerCharacter aPC)
	{
		if (lists == null || aPC == null)
		{
			return false;
		}
		Set<Integer> resultList = new TreeSet<Integer>();
		HashMapToList<CDOMList<Spell>, Integer> pcli = aPC.getSpellLevelInfo(sp);
		for (CDOMList<Spell> spellList : lists)
		{
			List<Integer> levels = pcli.getListFor(spellList);
			if (levels != null)
			{
				resultList.addAll(levels);
			}
		}
		return levelMatch == -1 && !resultList.isEmpty() || levelMatch >= 0
				&& resultList.contains(levelMatch);
	}

	public static Integer[] levelForKey(Spell sp,
			List<? extends CDOMList<Spell>> lists, PlayerCharacter aPC)
	{
		List<Integer> list = new ArrayList<Integer>();

		if (lists != null)
		{
			for (CDOMList<Spell> spellList : lists)
			{
				list.add(getFirstLvlForKey(sp, spellList, aPC));
			}
		}

		return list.toArray(new Integer[list.size()]);
	}

	/**
	 * Retrieve the first level that the pc receives the spell from the specified list. Note that this only returns 
	 * spells that the pc has available.
	 * 
	 * @param sp The spell to be found.
	 * @param list The spell list (e.g. a class spell list)
	 * @param aPC The character who must already have the spell.
	 * @return The level of the spell, or -1 if not found.
	 */
	public static int getFirstLvlForKey(Spell sp, CDOMList<Spell> list,
			PlayerCharacter aPC)
	{
		HashMapToList<CDOMList<Spell>, Integer> wLevelInfo = aPC.getSpellLevelInfo(sp);
		if ((wLevelInfo != null) && (wLevelInfo.size() != 0))
		{
			List<Integer> levelList = wLevelInfo.getListFor(list);
			if (levelList != null)
			{
				// We assume those calling this method know what they are doing!
				return levelList.get(0);
			}
		}
		return -1;
	}

	/**
	 * isLevel(int aLevel)
	 *
	 * @param aLevel
	 *            level of the spell
	 * @param aPC
	 * @return true if the spell is of the given level in any spell list
	 */
	public static boolean isLevel(Spell sp, int aLevel, PlayerCharacter aPC)
	{
		Integer levelKey = Integer.valueOf(aLevel);
		MasterListInterface masterLists = Globals.getMasterLists();
		for (PCClass pcc : aPC.getClassSet())
		{
			ClassSpellList csl = pcc.get(ObjectKey.CLASS_SPELLLIST);
			Collection<AssociatedPrereqObject> assoc = masterLists
					.getAssociations(csl, sp);
			if (assoc != null)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					if (PrereqHandler.passesAll(apo.getPrerequisiteList(), aPC,
							sp))
					{
						if (levelKey.equals(apo
								.getAssociation(AssociationKey.SPELL_LEVEL)))
						{
							return true;
						}
					}
				}
			}
		}
		for (Domain domain : aPC.getDomainSet())
		{
			DomainSpellList dsl = domain.get(ObjectKey.DOMAIN_SPELLLIST);
			Collection<AssociatedPrereqObject> assoc = masterLists
					.getAssociations(dsl, sp);
			if (assoc != null)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					if (PrereqHandler.passesAll(apo.getPrerequisiteList(), aPC,
							sp))
					{
						if (levelKey.equals(apo
								.getAssociation(AssociationKey.SPELL_LEVEL)))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static int getFirstLevelForKey(Spell sp,
			List<? extends CDOMList<Spell>> lists, PlayerCharacter aPC)
	{
		Integer[] levelInt = levelForKey(sp, lists, aPC);
		int result = -1;

		if (levelInt.length > 0)
		{
			for (int i = 0; i < levelInt.length; i++)
			{
				if (levelInt[i] > -1)
				{
					return levelInt[i];
				}
			}
		}

		return result;
	}
}
