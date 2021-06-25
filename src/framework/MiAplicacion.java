package framework;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MiAplicacion {
    private final List<Accion> acciones;

    public MiAplicacion() {
        acciones = new ArrayList<>();
        cargar();
    }

    private void cargar() {
        Set<Class<? extends Accion>> clases = new Reflections("").getSubTypesOf(Accion.class);
        clases.forEach(clase -> {
            try {
                acciones.add(clase.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException("");
            }
        });
    }

    public void iniciar() {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;

        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();

            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

            final Window window = new BasicWindow("Prueba framework");

            Panel contentPanel = new Panel(new GridLayout(2));

            GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
            gridLayout.setHorizontalSpacing(3);

            Label title = new Label("Elija una opción:");
            title.setLayoutData(GridLayout.createLayoutData(
                    GridLayout.Alignment.BEGINNING, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
                    GridLayout.Alignment.BEGINNING, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
                    true,       // Give the component extra horizontal space if available
                    false,        // Give the component extra vertical space if available
                    2,                  // Horizontal span
                    1));                  // Vertical span
            contentPanel.addComponent(title);

            ComboBox<Accion> comboBox = new ComboBox<Accion>(acciones);
            contentPanel.addComponent(new Label("Opciones:"));
            contentPanel.addComponent(comboBox.setReadOnly(false)
                    .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));

            contentPanel.addComponent(new Label("Presione continuar para ejecutar la acción"));
            contentPanel.addComponent(new Button("Continuar", () -> {
                comboBox.getSelectedItem().ejecutar();
                MessageDialog.showMessageDialog(textGUI, "Ejecutado", "Finalizó la ejecución con exito", MessageDialogButton.OK);
            }
            ).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));

            contentPanel.addComponent(
                    new EmptySpace()
                            .setLayoutData(
                                    GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPanel.addComponent(
                    new Separator(Direction.HORIZONTAL)
                            .setLayoutData(
                                    GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPanel.addComponent(
                    new Button("Salir", window::close).setLayoutData(
                            GridLayout.createHorizontallyEndAlignedLayoutData(2)));

            window.setComponent(contentPanel);

            textGUI.addWindowAndWait(window);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (screen != null) {
                try {
                    screen.stopScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
