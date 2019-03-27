package net.runelite.client.plugins.hideprayers;

import com.google.common.collect.ImmutableList;
import net.runelite.client.eventbus.Subscribe;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.ConfigChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "!Hide Prayers",
        description = "Hides specific Prayers in the Prayer tab."
)
public class HidePrayersPlugin extends Plugin
{
    private static final int PRAYER_COUNT = Prayer.values().length;

    private static final List<WidgetInfo> PRAYER_WIDGET_INFO_LIST = ImmutableList.of(
            WidgetInfo.PRAYER_THICK_SKIN,
            WidgetInfo.PRAYER_BURST_OF_STRENGTH,
            WidgetInfo.PRAYER_CLARITY_OF_THOUGHT,
            WidgetInfo.PRAYER_SHARP_EYE,
            WidgetInfo.PRAYER_MYSTIC_WILL,
            WidgetInfo.PRAYER_ROCK_SKIN,
            WidgetInfo.PRAYER_SUPERHUMAN_STRENGTH,
            WidgetInfo.PRAYER_IMPROVED_REFLEXES,
            WidgetInfo.PRAYER_RAPID_RESTORE,
            WidgetInfo.PRAYER_RAPID_HEAL,
            WidgetInfo.PRAYER_PROTECT_ITEM,
            WidgetInfo.PRAYER_HAWK_EYE,
            WidgetInfo.PRAYER_MYSTIC_LORE,
            WidgetInfo.PRAYER_STEEL_SKIN,
            WidgetInfo.PRAYER_ULTIMATE_STRENGTH,
            WidgetInfo.PRAYER_INCREDIBLE_REFLEXES,
            WidgetInfo.PRAYER_PROTECT_FROM_MAGIC,
            WidgetInfo.PRAYER_PROTECT_FROM_MISSILES,
            WidgetInfo.PRAYER_PROTECT_FROM_MELEE,
            WidgetInfo.PRAYER_EAGLE_EYE,
            WidgetInfo.PRAYER_MYSTIC_MIGHT,
            WidgetInfo.PRAYER_RETRIBUTION,
            WidgetInfo.PRAYER_REDEMPTION,
            WidgetInfo.PRAYER_SMITE,
            WidgetInfo.PRAYER_PRESERVE,
            WidgetInfo.PRAYER_CHIVALRY,
            WidgetInfo.PRAYER_PIETY,
            WidgetInfo.PRAYER_RIGOUR,
            WidgetInfo.PRAYER_AUGURY
    );

    @Inject
    private Client client;

    @Inject
    private HidePrayersConfig config;

    @Provides
    HidePrayersConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(HidePrayersConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        hidePrayers();
    }

    @Override
    protected void shutDown() throws Exception
    {
        restorePrayers();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGGED_IN)
        {
            hidePrayers();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals("hideprayers"))
        {
            hidePrayers();
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event)
    {
        if (event.getGroupId() == WidgetID.PRAYER_GROUP_ID || event.getGroupId() == WidgetID.QUICK_PRAYERS_GROUP_ID)
        {
            hidePrayers();
        }
    }

    private PrayerTabState getPrayerTabState()
    {
        HashTable<WidgetNode> componentTable = client.getComponentTable();
        for (WidgetNode widgetNode : componentTable.getNodes())
        {
            if (widgetNode.getId() == WidgetID.PRAYER_GROUP_ID)
            {
                return PrayerTabState.PRAYERS;
            }
            else if (widgetNode.getId() == WidgetID.QUICK_PRAYERS_GROUP_ID)
            {
                return PrayerTabState.QUICK_PRAYERS;
            }
        }
        return PrayerTabState.NONE;
    }

    private void restorePrayers()
    {
        if (client.getGameState() != GameState.LOGGED_IN)
            return;

        PrayerTabState prayerTabState = getPrayerTabState();

        if (prayerTabState == PrayerTabState.PRAYERS)
        {
            List<Widget> prayerWidgets = PRAYER_WIDGET_INFO_LIST.stream()
                    .map(client::getWidget)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (prayerWidgets.size() != PRAYER_WIDGET_INFO_LIST.size())
                return;

            for (int index = 0; index < PRAYER_COUNT; index++)
                prayerWidgets.get(Prayer.values()[index].ordinal()).setHidden(false);
        }
    }

    private void hidePrayers()
    {
        if (client.getGameState() != GameState.LOGGED_IN)
            return;

        PrayerTabState prayerTabState = getPrayerTabState();

        if (prayerTabState == PrayerTabState.PRAYERS)
        {
            List<Widget> prayerWidgets = PRAYER_WIDGET_INFO_LIST.stream()
                    .map(client::getWidget)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (prayerWidgets.size() != PRAYER_WIDGET_INFO_LIST.size())
                return;

            for (int index = 0; index < PRAYER_COUNT; index++)
            {
                Prayer prayer = Prayer.values()[index];
                Widget prayerWidget = prayerWidgets.get(prayer.ordinal());

                hidePrayer(prayerWidget);
            }
        }
    }

    private void hidePrayer(Widget prayerWidget)
    {
        String name = prayerWidget.getName().substring(12).toLowerCase();

        switch (name)
        {
            case "thick skin":
                prayerWidget.setHidden(config.hideThickSkin());
                break;
            case "burst of strength":
                prayerWidget.setHidden(config.hideBurstOfStrength());
                break;
            case "clarity of thought":
                prayerWidget.setHidden(config.hideClarityOfThought());
                break;
            case "sharp eye":
                prayerWidget.setHidden(config.hideSharpEye());
                break;
            case "mystic will":
                prayerWidget.setHidden(config.hideMysticWill());
                break;
            case "rock skin":
                prayerWidget.setHidden(config.hideRockSkin());
                break;
            case "superhuman strength":
                prayerWidget.setHidden(config.hideSuperhumanStrength());
                break;
            case "improved reflexes":
                prayerWidget.setHidden(config.hideImprovedReflexes());
                break;
            case "rapid restore":
                prayerWidget.setHidden(config.hideRapidRestore());
                break;
            case "rapid heal":
                prayerWidget.setHidden(config.hideRapidHeal());
                break;
            case "protect item":
                prayerWidget.setHidden(config.hideProtectItem());
                break;
            case "hawk eye":
                prayerWidget.setHidden(config.hideHawkEye());
                break;
            case "mystic lore":
                prayerWidget.setHidden(config.hideMysticLore());
                break;
            case "steel skin":
                prayerWidget.setHidden(config.hideSteelSkin());
                break;
            case "ultimate strength":
                prayerWidget.setHidden(config.hideUltimateStrength());
                break;
            case "incredible reflexes":
                prayerWidget.setHidden(config.hideIncredibleReflexes());
                break;
            case "protect from magic":
                prayerWidget.setHidden(config.hideProtectFromMagic());
                break;
            case "protect from missiles":
                prayerWidget.setHidden(config.hideProtectFromMissiles());
                break;
            case "protect from melee":
                prayerWidget.setHidden(config.hideProtectFromMelee());
                break;
            case "eagle eye":
                prayerWidget.setHidden(config.hideEagleEye());
                break;
            case "mystic might":
                prayerWidget.setHidden(config.hideMysticMight());
                break;
            case "retribution":
                prayerWidget.setHidden(config.hideRetribution());
                break;
            case "redemption":
                prayerWidget.setHidden(config.hideRedemption());
                break;
            case "smite":
                prayerWidget.setHidden(config.hideSmite());
                break;
            case "preserve":
                prayerWidget.setHidden(config.hidePreserve());
                break;
            case "chivalry":
                prayerWidget.setHidden(config.hideChivalry());
                break;
            case "piety":
                prayerWidget.setHidden(config.hidePiety());
                break;
            case "rigour":
                prayerWidget.setHidden(config.hideRigour());
                break;
            case "augury":
                prayerWidget.setHidden(config.hideAugury());
                break;
            default: break;
        }
    }
}
