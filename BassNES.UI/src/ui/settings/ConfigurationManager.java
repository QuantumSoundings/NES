package ui.settings;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import core.NesSettings;
import jdk.internal.util.xml.impl.Input;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.input.ControllerInterface;
import ui.input.HotKeyInterface;

public class ConfigurationManager {
    File configFile;
    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;
    Document doc;
    private ControllerInterface inputInterface;
    private HotKeyInterface hotKeyInterface;
    public ConfigurationManager(File f,ControllerInterface in,HotKeyInterface hot){
        inputInterface = in;
        hotKeyInterface = hot;
        if(!f.exists())
            f = null;
        if(f!=null) {
            try {

                dbFactory = DocumentBuilderFactory.newInstance();
                dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(f);
                loadConfig();
            }catch(Exception e){
                System.out.println(e.getLocalizedMessage());
                System.out.println(Arrays.toString(e.getStackTrace()));
            }

        }
        else {
            createDefaultConfig();
            loadConfig();
        }
    }
    private String getNodeValue(String tag){
        return doc.getElementsByTagName(tag).item(0).getTextContent();
    }
    private void setNodeValue(String tag, String value){
        //System.out.println("Current value: "+doc.getElementsByTagName(tag).item(0).getTextContent()+" wanted value: "+value);
        doc.getElementsByTagName(tag).item(0).setTextContent(value);
        //System.out.println("Modified value: "+doc.getElementsByTagName(tag).item(0).getTextContent());
    }
    private void loadConfig(){
        System.out.println("Loading Config...");
        //doc.getDocumentElement().normalize();
        //UI Settings
        UISettings.autoLoad = getNodeValue("autoload").equals("true");
        UISettings.ShowFPS = getNodeValue("showfps").equals("true");
        UISettings.lockVideoToAudio = getNodeValue("lockvideotoaudio").equals("true");
        UISettings.AudioEnabled = getNodeValue("audioenabled").equals("true");
        UISettings.lastLoadedDir = getNodeValue("lastloadeddir");
        //Video filter settings
        UISettings.currentFilter = UISettings.VideoFilter.valueOf(getNodeValue("currentfilter"));
        UISettings.scanlinesEnabled= getNodeValue("scanlinesenabled").equals("true");
        UISettings.scanlineThickness= Double.parseDouble(getNodeValue("scanlinethickness"));
        //Ntsc settings
        UISettings.ntsc_hue = Double.parseDouble(getNodeValue("ntsc_hue"));
        UISettings.ntsc_saturation = Double.parseDouble(getNodeValue("ntsc_saturation"));
        UISettings.ntsc_contrast = Double.parseDouble(getNodeValue("ntsc_contrast"));
        UISettings.ntsc_brightness = Double.parseDouble(getNodeValue("ntsc_brightness"));
        UISettings.ntsc_sharpness = Double.parseDouble(getNodeValue("ntsc_sharpness"));
        UISettings.ntsc_gamma = Double.parseDouble(getNodeValue("ntsc_gamma"));
        UISettings.ntsc_resolution = Double.parseDouble(getNodeValue("ntsc_resolution"));
        UISettings.ntsc_artifacts = Double.parseDouble(getNodeValue("ntsc_artifacts"));
        UISettings.ntsc_fringing = Double.parseDouble(getNodeValue("ntsc_fringing"));
        UISettings.ntsc_bleed = Double.parseDouble(getNodeValue("ntsc_bleed"));
        UISettings.ntsc_merge = getNodeValue("ntsc_merge").equals("true");
        //Controller Settings
        inputInterface.configureControllerInput(0, ControllerInterface.ControllerButtons.A,getNodeValue("c1a"));
        inputInterface.configureControllerInput(0, ControllerInterface.ControllerButtons.B,getNodeValue("c1b"));
        inputInterface.configureControllerInput(0, ControllerInterface.ControllerButtons.Select,getNodeValue("c1select"));
        inputInterface.configureControllerInput(0, ControllerInterface.ControllerButtons.Start,getNodeValue("c1start"));
        inputInterface.configureControllerInput(0, ControllerInterface.ControllerButtons.Up,getNodeValue("c1up"));
        inputInterface.configureControllerInput(0, ControllerInterface.ControllerButtons.Down,getNodeValue("c1down"));
        inputInterface.configureControllerInput(0, ControllerInterface.ControllerButtons.Left,getNodeValue("c1left"));
        inputInterface.configureControllerInput(0, ControllerInterface.ControllerButtons.Right,getNodeValue("c1right"));

        inputInterface.configureControllerInput(1, ControllerInterface.ControllerButtons.A,getNodeValue("c2a"));
        inputInterface.configureControllerInput(1, ControllerInterface.ControllerButtons.B,getNodeValue("c2b"));
        inputInterface.configureControllerInput(1, ControllerInterface.ControllerButtons.Select,getNodeValue("c2select"));
        inputInterface.configureControllerInput(1, ControllerInterface.ControllerButtons.Start,getNodeValue("c2start"));
        inputInterface.configureControllerInput(1, ControllerInterface.ControllerButtons.Up,getNodeValue("c2up"));
        inputInterface.configureControllerInput(1, ControllerInterface.ControllerButtons.Down,getNodeValue("c2down"));
        inputInterface.configureControllerInput(1, ControllerInterface.ControllerButtons.Left,getNodeValue("c2left"));
        inputInterface.configureControllerInput(1, ControllerInterface.ControllerButtons.Right,getNodeValue("c2right"));
        //Hot keys
        inputInterface.configureQuickKeyInput(ControllerInterface.QuickKeyButtons.SaveState,getNodeValue("quicksave"));
        inputInterface.configureQuickKeyInput(ControllerInterface.QuickKeyButtons.LoadState,getNodeValue("quickload"));
        inputInterface.configureQuickKeyInput(ControllerInterface.QuickKeyButtons.InputRecord,getNodeValue("inputrecord"));
        inputInterface.configureQuickKeyInput(ControllerInterface.QuickKeyButtons.InputPlay,getNodeValue("inputplay"));
        inputInterface.configureQuickKeyInput(ControllerInterface.QuickKeyButtons.AudioRecord,getNodeValue("togglerecording"));


        for(int i = 0; i < 10; i++){
            Element savestate = (Element)doc.getElementsByTagName("savestate"+i).item(0);
            Element key = (Element) savestate.getElementsByTagName("keynum").item(0);
            Element mod = (Element) savestate.getElementsByTagName("modifier").item(0);
            hotKeyInterface.updateInputMapHotKey(HotKeyInterface.HotKeys.values()[i], KeyStroke.getKeyStroke(Integer.parseInt(key.getFirstChild().getTextContent()),Integer.parseInt(mod.getFirstChild().getTextContent())));
        }
        for(int i = 0; i < 10; i++){
            Element savestate = (Element)doc.getElementsByTagName("loadstate"+i).item(0);
            Element key = (Element) savestate.getElementsByTagName("keynum").item(0);
            Element mod = (Element) savestate.getElementsByTagName("modifier").item(0);
            hotKeyInterface.updateInputMapHotKey(HotKeyInterface.HotKeys.values()[i+10], KeyStroke.getKeyStroke(Integer.parseInt(key.getFirstChild().getTextContent()),Integer.parseInt(mod.getFirstChild().getTextContent())));
        }

        NesSettings.frameLimit = getNodeValue("framelimit").equals("true");
        NesSettings.politeFrameTiming= getNodeValue("politeframetiming").equals("true");

        NesSettings.RenderBackground = getNodeValue("renderbackground").equals("true");
        NesSettings.RenderSprites = getNodeValue("rendersprites").equals("true");
        NesSettings.disableSpriteLimit = getNodeValue("disablespritelimit").equals("true");
        NesSettings.RenderMethod = Integer.parseInt(getNodeValue("rendermethod"));
        NesSettings.selectedPalette= getNodeValue("selectedpalette");

        NesSettings.masterMixLevel = Integer.parseInt(getNodeValue("mastermixlevel"));
        NesSettings.pulse1MixLevel = Integer.parseInt(getNodeValue("pulse1mixlevel"));
        NesSettings.pulse2MixLevel = Integer.parseInt(getNodeValue("pulse2mixlevel"));
        NesSettings.triangleMixLevel = Integer.parseInt(getNodeValue("trianglemixlevel"));
        NesSettings.noiseMixLevel = Integer.parseInt(getNodeValue("noisemixlevel"));
        NesSettings.dmcMixLevel = Integer.parseInt(getNodeValue("dmcmixlevel"));
        NesSettings.vrc6MixLevel = Integer.parseInt(getNodeValue("vrc6mixlevel"));
        NesSettings.mmc5MixLevel = Integer.parseInt(getNodeValue("mmc5mixlevel"));
        NesSettings.namcoMixLevel = Integer.parseInt(getNodeValue("namcomixlevel"));
        NesSettings.sunsoft5BMixLevel = Integer.parseInt(getNodeValue("sunsoft5bmixlevel"));

        NesSettings.pulse1Panning = Integer.parseInt(getNodeValue("pulse1panning"));
        NesSettings.pulse2Panning = Integer.parseInt(getNodeValue("pulse2panning"));
        NesSettings.trianglePanning = Integer.parseInt(getNodeValue("trianglepanning"));
        NesSettings.noisePanning = Integer.parseInt(getNodeValue("noisepanning"));
        NesSettings.dmcPanning = Integer.parseInt(getNodeValue("dmcpanning"));
        NesSettings.vrc6Panning = Integer.parseInt(getNodeValue("vrc6panning"));
        NesSettings.mmc5Panning = Integer.parseInt(getNodeValue("mmc5panning"));
        NesSettings.namcoPanning = Integer.parseInt(getNodeValue("namcopanning"));
        NesSettings.sunsoft5BPanning = Integer.parseInt(getNodeValue("sunsoft5bpanning"));

        NesSettings.audioBufferSize = Integer.parseInt(getNodeValue("audiobuffersize"));
        NesSettings.sampleRate = Integer.parseInt(getNodeValue("samplerate"));
        NesSettings.highQualitySampling = getNodeValue("highqualitysampling").equals("true");
        NesSettings.nsfPlayerSongLength = Integer.parseInt(getNodeValue("nsfplayersonglength"));
        NesSettings.nsfPlayerFadeLength = Integer.parseInt(getNodeValue("nsfplayerfadelength"));


    }
    public void saveSettings(File f){
        System.out.println("Saving Config...");
        setNodeValue("autoload",UISettings.autoLoad+"");
        setNodeValue("showfps",UISettings.ShowFPS+"");
        setNodeValue("lockvideotoaudio",UISettings.lockVideoToAudio+"");
        setNodeValue("audioenabled",UISettings.AudioEnabled+"");
        setNodeValue("lastloadeddir",UISettings.lastLoadedDir+"");
        //Video Filter Settings
        setNodeValue("currentfilter",UISettings.currentFilter+"");
        setNodeValue("scanlinethickness",UISettings.scanlineThickness+"");
        setNodeValue("scanlinesenabled",UISettings.scanlinesEnabled+"");
        //Ntsc settings
        setNodeValue("ntsc_hue",UISettings.ntsc_hue+"");
        setNodeValue("ntsc_saturation",UISettings.ntsc_saturation+"");
        setNodeValue("ntsc_contrast",UISettings.ntsc_contrast+"");
        setNodeValue("ntsc_brightness",UISettings.ntsc_brightness+"");
        setNodeValue("ntsc_sharpness",UISettings.ntsc_sharpness+"");
        setNodeValue("ntsc_gamma",UISettings.ntsc_gamma+"");
        setNodeValue("ntsc_resolution",UISettings.ntsc_resolution+"");
        setNodeValue("ntsc_artifacts",UISettings.ntsc_artifacts+"");
        setNodeValue("ntsc_fringing",UISettings.ntsc_fringing+"");
        setNodeValue("ntsc_bleed",UISettings.ntsc_bleed+"");
        setNodeValue("ntsc_merge",UISettings.ntsc_merge+"");
        //Controller settings
        setNodeValue("c1a",inputInterface.getControllerInputConfig(0, ControllerInterface.ControllerButtons.A));
        setNodeValue("c1b",inputInterface.getControllerInputConfig(0, ControllerInterface.ControllerButtons.B));
        setNodeValue("c1start",inputInterface.getControllerInputConfig(0, ControllerInterface.ControllerButtons.Start));
        setNodeValue("c1select",inputInterface.getControllerInputConfig(0, ControllerInterface.ControllerButtons.Select));
        setNodeValue("c1up",inputInterface.getControllerInputConfig(0, ControllerInterface.ControllerButtons.Up));
        setNodeValue("c1down",inputInterface.getControllerInputConfig(0, ControllerInterface.ControllerButtons.Down));
        setNodeValue("c1left",inputInterface.getControllerInputConfig(0, ControllerInterface.ControllerButtons.Left));
        setNodeValue("c1right",inputInterface.getControllerInputConfig(0, ControllerInterface.ControllerButtons.Right));

        setNodeValue("c2a",inputInterface.getControllerInputConfig(1, ControllerInterface.ControllerButtons.A));
        setNodeValue("c2b",inputInterface.getControllerInputConfig(1, ControllerInterface.ControllerButtons.B));
        setNodeValue("c2start",inputInterface.getControllerInputConfig(1, ControllerInterface.ControllerButtons.Start));
        setNodeValue("c2select",inputInterface.getControllerInputConfig(1, ControllerInterface.ControllerButtons.Select));
        setNodeValue("c2up",inputInterface.getControllerInputConfig(1, ControllerInterface.ControllerButtons.Up));
        setNodeValue("c2down",inputInterface.getControllerInputConfig(1, ControllerInterface.ControllerButtons.Down));
        setNodeValue("c2left",inputInterface.getControllerInputConfig(1, ControllerInterface.ControllerButtons.Left));
        setNodeValue("c2right",inputInterface.getControllerInputConfig(1, ControllerInterface.ControllerButtons.Right));

        setNodeValue("quicksave",inputInterface.getQuickKeyInputConfig(ControllerInterface.QuickKeyButtons.SaveState));
        setNodeValue("quickload",inputInterface.getQuickKeyInputConfig(ControllerInterface.QuickKeyButtons.LoadState));
        setNodeValue("inputrecord",inputInterface.getQuickKeyInputConfig(ControllerInterface.QuickKeyButtons.InputRecord));
        setNodeValue("inputplay",inputInterface.getQuickKeyInputConfig(ControllerInterface.QuickKeyButtons.InputPlay));
        setNodeValue("togglerecording",inputInterface.getQuickKeyInputConfig(ControllerInterface.QuickKeyButtons.AudioRecord));
        //Save HotKeys
        InputMap map = hotKeyInterface.getInputMap();
        KeyStroke[] keys = map.keys();
        for(int i = 0; i < 10; i++){
            HotKeyInterface.HotKeys key = HotKeyInterface.HotKeys.values()[i];
            Node savestate = doc.getElementsByTagName((key+"").toLowerCase()).item(0);
            NodeList nodes = savestate.getChildNodes();
            KeyStroke foundkey = KeyStroke.getKeyStroke(KeyEvent.VK_0,0);
            for(KeyStroke k:keys)
                if(map.get(k).equals(key)) {
                    foundkey = k;
                    break;
                }
            nodes.item(0).setNodeValue(foundkey.getKeyCode()+"");
            nodes.item(1).setNodeValue(foundkey.getModifiers()+"");
        }
        for(int i = 0; i <10; i++){
            HotKeyInterface.HotKeys key = HotKeyInterface.HotKeys.values()[i+10];
            Node loadstate = doc.getElementsByTagName((key+"").toLowerCase()).item(0);
            NodeList nodes = loadstate.getChildNodes();
            KeyStroke foundkey = KeyStroke.getKeyStroke(KeyEvent.VK_0,0);
            for(KeyStroke k:keys)
                if(map.get(k).equals(key)) {
                    foundkey = k;
                    break;
                }
            nodes.item(0).setNodeValue(foundkey.getKeyCode()+"");
            nodes.item(1).setNodeValue(foundkey.getModifiers()+"");
        }



        // NES SETTINGS
        setNodeValue("politeframetiming",NesSettings.politeFrameTiming+"");
        setNodeValue("framelimit",NesSettings.frameLimit+"");

        setNodeValue("renderbackground",NesSettings.RenderBackground+"");
        setNodeValue("rendersprites",NesSettings.RenderSprites+"");
        setNodeValue("disablespritelimit",NesSettings.disableSpriteLimit+"");
        setNodeValue("rendermethod",NesSettings.RenderMethod+"");
        setNodeValue("selectedpalette",NesSettings.selectedPalette+"");

        setNodeValue("mastermixlevel",NesSettings.masterMixLevel+"");
        setNodeValue("pulse1mixlevel",NesSettings.pulse1MixLevel+"");
        setNodeValue("pulse2mixlevel",NesSettings.pulse2MixLevel+"");
        setNodeValue("trianglemixlevel",NesSettings.triangleMixLevel+"");
        setNodeValue("noisemixlevel",NesSettings.noiseMixLevel+"");
        setNodeValue("dmcmixlevel",NesSettings.dmcMixLevel+"");
        setNodeValue("vrc6mixlevel",NesSettings.vrc6MixLevel+"");
        setNodeValue("namcomixlevel",NesSettings.namcoMixLevel+"");
        setNodeValue("mmc5mixlevel",NesSettings.mmc5MixLevel+"");
        setNodeValue("sunsoft5bmixlevel",NesSettings.sunsoft5BMixLevel+"");

        setNodeValue("pulse1panning",NesSettings.pulse1Panning+"");
        setNodeValue("pulse2panning",NesSettings.pulse2Panning+"");
        setNodeValue("trianglepanning",NesSettings.trianglePanning+"");
        setNodeValue("noisepanning",NesSettings.noisePanning+"");
        setNodeValue("dmcpanning",NesSettings.dmcPanning+"");
        setNodeValue("vrc6panning",NesSettings.vrc6Panning+"");
        setNodeValue("namcopanning",NesSettings.namcoPanning+"");
        setNodeValue("mmc5panning",NesSettings.mmc5Panning+"");
        setNodeValue("sunsoft5bpanning",NesSettings.sunsoft5BPanning+"");

        setNodeValue("audiobuffersize",NesSettings.audioBufferSize+"");
        setNodeValue("samplerate",NesSettings.sampleRate+"");
        setNodeValue("highqualitysampling",NesSettings.highQualitySampling+"");
        setNodeValue("nsfplayersonglength",NesSettings.nsfPlayerSongLength+"");
        setNodeValue("nsfplayerfadelength",NesSettings.nsfPlayerFadeLength+"");



        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("settings.xml"));
            transformer.transform(source, result);
            //StreamResult consoleResult = new StreamResult(System.out);
            //transformer.transform(source, consoleResult);
        }catch(Exception e){

        }
    }
    private void createDefaultConfig(){
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
            Element root = doc.createElement("settings");
            doc.appendChild(root);
            Element uisettings = doc.createElement("uisettings");
            root.appendChild(uisettings);
            createChild(uisettings,"autoload","true");
            createChild(uisettings,"showfps","true");
            createChild(uisettings,"lockvideotoaudio","false");
            createChild(uisettings,"audioenabled","true");
            createChild(uisettings,"lastloadeddir",System.getProperty("user.dir"));
            createChild(uisettings,"controlwhilenotfocused","true");


            Element videosettings = doc.createElement("videosettings");
            uisettings.appendChild(videosettings);

            Element ntscsettings = doc.createElement("ntsc");
            createChild(ntscsettings,"ntsc_hue","0");
            createChild(ntscsettings,"ntsc_saturation","0");
            createChild(ntscsettings,"ntsc_contrast","0");
            createChild(ntscsettings,"ntsc_brightness","0");
            createChild(ntscsettings,"ntsc_sharpness","0");
            createChild(ntscsettings,"ntsc_gamma","0");
            createChild(ntscsettings,"ntsc_resolution","0");
            createChild(ntscsettings,"ntsc_artifacts","0");
            createChild(ntscsettings,"ntsc_fringing","0");
            createChild(ntscsettings,"ntsc_bleed","0");
            createChild(ntscsettings,"ntsc_merge","true");
            videosettings.appendChild(ntscsettings);

            createChild(videosettings,"currentfilter","None");
            createChild(videosettings,"scanlinethickness",".5");
            createChild(videosettings,"scanlinesenabled","false");


            Element controllersettings = doc.createElement("controller");
            Element controlone = doc.createElement("controller1");
            Element controltwo = doc.createElement("controller2");
            Element controlhot = doc.createElement("controllerhotkeys");
            controllersettings.appendChild(controlone);
            controllersettings.appendChild(controltwo);
            controllersettings.appendChild(controlhot);

            createChild(controlone,"c1a","Standard PS/2 Keyboard:0;A;1.0");
            createChild(controlone,"c1b","Standard PS/2 Keyboard:0;S;1.0");
            createChild(controlone,"c1select","Standard PS/2 Keyboard:0;W;1.0");
            createChild(controlone,"c1start","Standard PS/2 Keyboard:0;Q;1.0");
            createChild(controlone,"c1up","Standard PS/2 Keyboard:0;Up;1.0");
            createChild(controlone,"c1down","Standard PS/2 Keyboard:0;Down;1.0");
            createChild(controlone,"c1left","Standard PS/2 Keyboard:0;Left;1.0");
            createChild(controlone,"c1right","Standard PS/2 Keyboard:0;Right;1.0");

            createChild(controltwo,"c2a","Standard PS/2 Keyboard:0;A;1.0");
            createChild(controltwo,"c2b","Standard PS/2 Keyboard:0;S;1.0");
            createChild(controltwo,"c2select","Standard PS/2 Keyboard:0;W;1.0");
            createChild(controltwo,"c2start","Standard PS/2 Keyboard:0;Q;1.0");
            createChild(controltwo,"c2up","Standard PS/2 Keyboard:0;Up;1.0");
            createChild(controltwo,"c2down","Standard PS/2 Keyboard:0;Down;1.0");
            createChild(controltwo,"c2left","Standard PS/2 Keyboard:0;Left;1.0");
            createChild(controltwo,"c2right","Standard PS/2 Keyboard:0;Right;1.0");

            createChild(controlhot,"quicksave","Standard PS/2 Keyboard:0;Z;1.0");
            createChild(controlhot,"quickload","Standard PS/2 Keyboard:0;X;1.0");
            createChild(controlhot,"inputrecord","Standard PS/2 Keyboard:0;C;1.0");
            createChild(controlhot,"inputplay","Standard PS/2 Keyboard:0;V;1.0");
            createChild(controlhot,"togglerecording","Standard PS/2 Keyboard:0;O;1.0");

            uisettings.appendChild(controllersettings);
            Element hotkeys = doc.createElement("hotkeys");
            uisettings.appendChild(hotkeys);

            for(int i = 0; i < 10; i++){
                Element savestate = doc.createElement("savestate"+i);
                createChild(savestate,"keynum", (KeyEvent.VK_0+i)+"");
                createChild(savestate,"modifier",InputEvent.CTRL_DOWN_MASK+"");
                hotkeys.appendChild(savestate);
            }
            for(int i = 0; i < 10; i++){
                Element savestate = doc.createElement("loadstate"+i);
                createChild(savestate,"keynum",(KeyEvent.VK_0+i)+"");
                createChild(savestate,"modifier",InputEvent.SHIFT_DOWN_MASK+"");
                hotkeys.appendChild(savestate);
            }



            //NES SETTINGS.............................
            Element nes = doc.createElement("emulation");
            root.appendChild(nes);
            Element thread = doc.createElement("threaded");
            nes.appendChild(thread);
            createChild(thread,"politeframetiming", NesSettings.politeFrameTiming+"");
            createChild(thread,"framelimit", NesSettings.frameLimit+"");

            Element graphics = doc.createElement("graphical");
            nes.appendChild(graphics);
            createChild(graphics,"renderbackground", NesSettings.RenderBackground+"");
            createChild(graphics,"rendersprites", NesSettings.RenderSprites+"");
            createChild(graphics,"disablespritelimit", NesSettings.disableSpriteLimit+"");
            createChild(graphics,"rendermethod", NesSettings.RenderMethod+"");
            createChild(graphics,"selectedpalette", NesSettings.selectedPalette+"");

            Element audio = doc.createElement("audio");
            nes.appendChild(audio);
            createChild(audio,"mastermixlevel", NesSettings.masterMixLevel+"");
            createChild(audio,"pulse1mixlevel", NesSettings.pulse1MixLevel+"");
            createChild(audio,"pulse2mixlevel", NesSettings.pulse2MixLevel+"");
            createChild(audio,"trianglemixlevel", NesSettings.triangleMixLevel+"");
            createChild(audio,"noisemixlevel", NesSettings.noiseMixLevel+"");
            createChild(audio,"dmcmixlevel", NesSettings.dmcMixLevel+"");
            createChild(audio,"vrc6mixlevel", NesSettings.vrc6MixLevel+"");
            createChild(audio,"namcomixlevel", NesSettings.namcoMixLevel+"");
            createChild(audio,"mmc5mixlevel", NesSettings.mmc5MixLevel+"");
            createChild(audio,"sunsoft5bmixlevel", NesSettings.sunsoft5BMixLevel+"");

            createChild(audio,"pulse1panning", NesSettings.pulse1Panning+"");
            createChild(audio,"pulse2panning", NesSettings.pulse2Panning+"");
            createChild(audio,"trianglepanning", NesSettings.trianglePanning+"");
            createChild(audio,"noisepanning", NesSettings.noisePanning+"");
            createChild(audio,"dmcpanning", NesSettings.dmcPanning+"");
            createChild(audio,"vrc6panning", NesSettings.vrc6Panning+"");
            createChild(audio,"namcopanning", NesSettings.namcoPanning+"");
            createChild(audio,"mmc5panning", NesSettings.mmc5Panning+"");
            createChild(audio,"sunsoft5bpanning", NesSettings.sunsoft5BPanning+"");

            createChild(audio,"highqualitysampling",NesSettings.highQualitySampling+"");
            createChild(audio,"nsfplayersonglength",NesSettings.nsfPlayerSongLength+"");
            createChild(audio,"nsfplayerfadelength",NesSettings.nsfPlayerFadeLength+"");
            createChild(audio,"samplerate",NesSettings.sampleRate+"");
            createChild(audio,"audiobuffersize",NesSettings.audioBufferSize+"");


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("settings.xml"));
            transformer.transform(source, result);

            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        }catch(Exception e){
        }
    }

    private void createChild(Element parent, String tag, String data){
        Element e = doc.createElement(tag);
        e.appendChild(doc.createTextNode(data));
        parent.appendChild(e);
    }

}
