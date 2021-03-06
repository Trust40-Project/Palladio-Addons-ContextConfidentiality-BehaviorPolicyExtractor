package preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import settings.Settings;

/**
 * Creates preference page for plugin in preferences
 * 
 * This class is referenced in plugin.xml
 * 
 * @author Thomas Lieb
 *
 */
public class PreferenceHandler extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    // The plug-in ID
    private static final String PLUGIN_ID = "PolicyExtractor";
    private static final IPreferenceStore PREF_STORE = new ScopedPreferenceStore(InstanceScope.INSTANCE, PLUGIN_ID);

    // Static names/ids for the different fields
    private static String namePath = "0";
    private static String nameUsageModel = "2";
    private static String nameAssembly = "3";
    private static String nameRepositoryModel = "4";
    private static String nameContextModel = "5";
    private static String nameCombine = "100";

    private static final int WIDTH = 75;

    public PreferenceHandler() {
        super(GRID);
        setPreferenceStore(PREF_STORE);

        setDefault();
    }

    /**
     * Set default values
     * 
     * This function is separate because default values also needed if preferences where not
     * initialised yet by eclipse environment
     */
    public static void setDefault() {
        // Default values
        PREF_STORE.setDefault(namePath, "");
        PREF_STORE.setDefault(nameRepositoryModel, "newRepository.repository");
        PREF_STORE.setDefault(nameAssembly, "newAssembly.system");
        PREF_STORE.setDefault(nameUsageModel, "newUsageModel.usagemodel");
        PREF_STORE.setDefault(nameContextModel, "My.context");
    }

    @Override
    public void init(IWorkbench arg0) {
        setDescription("Preference page for the plugin " + "");
    }

    /**
     * Create Fields in PreferencePage
     */
    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();

        // Paths
        StringFieldEditor path = new StringFieldEditor(namePath, "Project Path:", WIDTH, parent);
        addField(path);
        StringFieldEditor useagemodel = new StringFieldEditor(nameUsageModel, "UsageModelFile:", WIDTH, parent);
        addField(useagemodel);
        StringFieldEditor assembly = new StringFieldEditor(nameAssembly, "AssemblyFile:", WIDTH, parent);
        addField(assembly);
        StringFieldEditor repository = new StringFieldEditor(nameRepositoryModel, "RepositoryModelFile:", WIDTH,
                parent);
        addField(repository);
        StringFieldEditor context = new StringFieldEditor(nameContextModel, "ContextModelFile:", WIDTH, parent);
        addField(context);

        // Settings
        String[][] s2 = { { "true", "true" }, { "false", "false" } };
        RadioGroupFieldEditor createContextCharacteristic = new RadioGroupFieldEditor(nameCombine,
                "Combine usageSencario and systemCall:", 2, s2, parent);
        addField(createContextCharacteristic);

    }

    public static String getProjectPath() {
        return PREF_STORE.getString(namePath);
    }

    public static String getPathrepositorymodel() {
        return PREF_STORE.getString(nameRepositoryModel);
    }

    public static String getPathassembly() {
        return PREF_STORE.getString(nameAssembly);
    }

    public static String getPathusagemodel() {
        return PREF_STORE.getString(nameUsageModel);
    }

    public static String getPathContextModel() {
        return PREF_STORE.getString(nameContextModel);
    }

    public static Settings getSettingsFromPreferences() {
        String path = getProjectPath();

        boolean combine = false;
        if (PREF_STORE.getString(nameCombine)
            .equalsIgnoreCase("true")) {
            combine = true;
        }

        return new Settings(path, combine);
    }
}
