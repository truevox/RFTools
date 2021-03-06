package mcjty.rftools.blocks.logic;

import mcjty.container.GenericGuiContainer;
import mcjty.gui.Window;
import mcjty.gui.events.TextEvent;
import mcjty.gui.layout.HorizontalLayout;
import mcjty.gui.layout.VerticalLayout;
import mcjty.gui.widgets.Label;
import mcjty.gui.widgets.Panel;
import mcjty.gui.widgets.TextField;
import mcjty.gui.widgets.Widget;
import mcjty.rftools.RFTools;
import mcjty.rftools.network.Argument;
import net.minecraft.inventory.Container;

import java.awt.*;

public class GuiCounter extends GenericGuiContainer<CounterTileEntity> {
    public static final int COUNTER_WIDTH = 200;
    public static final int COUNTER_HEIGHT = 30;

    private TextField counterField;
    private TextField currentField;

    public GuiCounter(CounterTileEntity counterTileEntity, Container container) {
        super(counterTileEntity, container, RFTools.GUI_MANUAL_MAIN, "counter");
        xSize = COUNTER_WIDTH;
        ySize = COUNTER_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        Panel toplevel = new Panel(mc, this).setFilledRectThickness(2).setLayout(new VerticalLayout());

        counterField = new TextField(mc, this).setTooltips("Set the counter in pulses").addTextEvent(new TextEvent() {
            @Override
            public void textChanged(Widget parent, String newText) {
                setCounter();
            }
        });
        int delay = tileEntity.getCounter();
        if (delay <= 0) {
            delay = 1;
        }
        counterField.setText(String.valueOf(delay));

        currentField = new TextField(mc, this).setTooltips("Set the current value", "(fires when it reaches counter)").addTextEvent(new TextEvent() {
            @Override
            public void textChanged(Widget parent, String newText) {
                setCurrent();
            }
        });
        int current = tileEntity.getCurrent();
        if (current < 0) {
            current = 0;
        }
        currentField.setText(String.valueOf(current));

        Panel bottomPanel = new Panel(mc, this).setLayout(new HorizontalLayout()).
                addChild(new Label(mc, this).setText("Counter:")).addChild(counterField).
                addChild(new Label(mc, this).setText("Current:")).addChild(currentField);
        toplevel.addChild(bottomPanel);

        toplevel.setBounds(new Rectangle(guiLeft, guiTop, COUNTER_WIDTH, COUNTER_HEIGHT));
        window = new Window(this, toplevel);
    }

    private void setCounter() {
        String d = counterField.getText();
        int counter;
        try {
            counter = Integer.parseInt(d);
        } catch (NumberFormatException e) {
            counter = 1;
        }
        tileEntity.setCounter(counter);
        sendServerCommand(CounterTileEntity.CMD_SETCOUNTER, new Argument("counter", counter));
    }

    private void setCurrent() {
        String d = currentField.getText();
        int current;
        try {
            current = Integer.parseInt(d);
        } catch (NumberFormatException e) {
            current = 0;
        }
        tileEntity.setCounter(current);
        sendServerCommand(CounterTileEntity.CMD_SETCURRENT, new Argument("current", current));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
    }
}
