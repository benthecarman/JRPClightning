package jrpc.clightning.plugins.rpcmethods.manifest;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import jrpc.clightning.CLightningConstant;
import jrpc.clightning.model.CLightningProprietiesMediator;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.log.CLightningLevelLog;
import jrpc.clightning.plugins.rpcmethods.AbstractRPCMethod;
import jrpc.clightning.plugins.rpcmethods.init.InitMethod;
import jrpc.clightning.plugins.rpcmethods.manifest.types.Features;
import jrpc.clightning.plugins.rpcmethods.manifest.types.Option;
import jrpc.service.CLightningLogger;
import jrpc.service.converters.JsonConverter;
import jrpc.service.converters.jsonwrapper.CLightningJsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author https://github.com/vincenzopalazzo
 */
public class ManifestMethod extends AbstractRPCMethod {

    private static final Class<ManifestMethod> TAG = ManifestMethod.class;

    private List<Option> options = new ArrayList<>();
    @SerializedName("rpcmethods")
    private List<AbstractRPCMethod> rpcMethods = new ArrayList<>();
    private List<String> subscriptions = new ArrayList<>();
    private List<String> hooks = new ArrayList<>();
    private Features features = new Features();
    private boolean dynamic = true;

    public ManifestMethod() {
        super("getmanifest", null, null);
    }

    @Override
    public void doRun(ICLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
        CLightningLogger.getInstance().debug(TAG, "**** result method getmanifest: \n" + response);
        JsonConverter converter = new JsonConverter();
        JsonObject getManifest = (JsonObject) converter.deserialization(converter.serialization(this), JsonObject.class);
        response.mapping(getManifest);
        plugin.log(CLightningLevelLog.DEBUG, "----------- getmanifest response -------");
        plugin.log(CLightningLevelLog.DEBUG, response);
    }

    public void addFeature(String node, String channel, String init, String invoice){
        //TODO if empty
        this.features = new Features(node, channel, init, invoice);
    }

    public void addMethods(List<AbstractRPCMethod> methods){
        if(methods == null){
            throw new IllegalArgumentException("List of methods empty or null");
        }
        methods.removeIf(method -> (method instanceof ManifestMethod || method instanceof InitMethod));
        this.rpcMethods.addAll(methods);
    }

    public void addMethod(AbstractRPCMethod method){
        if(method == null){
            throw new IllegalArgumentException("List of methods empty or null");
        }
        this.rpcMethods.add(method);
    }

    public void addOption(Option option){
        if(option == null){
            CLightningLogger.getInstance().error(TAG, "Option null in method addOption");
            throw new IllegalArgumentException("option null");
        }
        this.options.add(option);
    }

    public void addSubscriptions(String subscription){
        if(subscription == null){
            throw new IllegalArgumentException("Subscription null");
        }
        this.subscriptions.add(subscription);
    }

    public void addHook(String hook){
        if(hook == null){
            throw new IllegalArgumentException("Hook null");
        }
        this.hooks.add(hook);
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    //getter methods
    public List<Option> getOptions() {
        return options;
    }

    public List<AbstractRPCMethod> getRpcMethods() {
        return rpcMethods;
    }

    public List<String> getSubscriptions() {
        return subscriptions;
    }

    public List<String> getHooks() {
        return hooks;
    }

    public Features getFeatures() {
        return features;
    }

    public Boolean getDynamic() {
        return dynamic;
    }
}
