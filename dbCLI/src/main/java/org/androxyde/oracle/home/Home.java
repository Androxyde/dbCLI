package org.androxyde.oracle.home;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.os.CommandResult;
import org.androxyde.os.OS;
import org.androxyde.os.UserInfos;
import org.buildobjects.process.ProcBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserPrincipal;
import java.util.*;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

@Serdeable
@Builder
@Jacksonized
@Data
@Slf4j
public class Home {

    String homeLocation;

    @JsonIgnore
    String homeType;

    @JsonIgnore
    @Builder.Default
    Boolean isValid=Boolean.TRUE;

    String homeRelease;

    String homeVersion;

    String homeOwner;
    String homeConfig;
    String homeBase;
    String ownerPrimaryGroup;
    String homeInstallGroup;
    String inventoryLocation;

    @Builder.Default
    @JsonIgnore
    TreeSet<OneOff> dbPSU = new TreeSet<>();

    public static final Map<String, String> tools = Map.of("/apps/oracle/exploit","EXPLOIT");

    static final Set<Long> dbPsu19cRef = Set.of(36582781L,36233263L);
    static final Set<Long> agentPsu19cRef = Set.of(36335374L);

    public void fetchOwner() {
        try {
            UserPrincipal p = Files.getOwner(new File(homeLocation + "/bin/oracle").toPath());
            homeOwner=p.getName();
            UserInfos user = OS.getUserInfos(homeOwner);
            ownerPrimaryGroup=user.getPrimaryGroup().getName();
        } catch (Exception e) {}

        try {
            Properties prop = new Properties();
            prop.load(new FileReader(new File(homeLocation+"/oraInst.loc")));
            homeInstallGroup=prop.getProperty("inst_group");
            inventoryLocation=prop.getProperty("inventory_loc");
        } catch (Exception e) {}

    }

    public void fetchOracleBaseConfig() {
        if (Files.exists(Path.of(homeLocation+"/bin/orabaseconfig"))) {
            ProcBuilder pb = new ProcBuilder("sh")
                    .withArg("-c")
                    .withArg(homeLocation + "/bin/orabaseconfig;"+homeLocation + "/bin/orabase")
                    .withVar("ORACLE_HOME",homeLocation)
                    .withNoTimeout();
            CommandResult r = OS.execute(pb);
            if (r.getReturnCode() == 0) {
                homeConfig = r.getStdout().get(0).trim() + "/dbs";
                homeBase=r.getStdout().get(1).trim();
            }
        }
        else {
            homeConfig=homeLocation+"/dbs";
        }
    }

    public void fetchLocalInventory() {

        XmlMapper xm = new XmlMapper();
        XMLInputFactory xif = XMLInputFactory.newInstance();
        Map<String, InventoryComp> comps = new HashMap<>();

        Set<OneOff> oneoffs = new HashSet<>();

        try {
            XMLStreamReader xr = xif.createXMLStreamReader(new FileInputStream(homeLocation + "/inventory/ContentsXML/comps.xml"));
            while (xr.hasNext()) {
                xr.next();
                if (xr.getEventType() == START_ELEMENT) {
                    if ("COMP".equals(xr.getLocalName())) {
                        InventoryComp comp = xm.readValue(xr, InventoryComp.class);
                        if (List.of("oracle.server", "oracle.crs", "oracle.sysman.top.agent", "oracle.sysman.top.oms", "oracle.client").contains(comp.getName())) {
                            comps.put(comp.getName(), comp);
                        }
                    } else if ("ONEOFF".equals(xr.getLocalName())) {
                        OneOff oneoff = xm.readValue(xr, OneOff.class);
                        oneoffs.add(oneoff);
                    }
                }
            }
            xr.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            isValid=Boolean.FALSE;
        }

        if (!isValid) return;

        try {
            XMLStreamReader xr = xif.createXMLStreamReader(new FileInputStream(homeLocation + "/inventory/ContentsXML/oui-patch.xml"));
            while (xr.hasNext()) {
                xr.next();
                if (xr.getEventType() == START_ELEMENT) {
                    if ("ONEOFF".equals(xr.getLocalName())) {
                        OneOff oneoff = xm.readValue(xr, OneOff.class);
                        oneoffs.add(oneoff);
                    }
                }
            }
            xr.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            XMLStreamReader xr = xif.createXMLStreamReader(new FileInputStream(homeLocation + "/inventory/registry.xml"));
            while (xr.hasNext()) {
                xr.next();
                if (xr.getEventType() == START_ELEMENT) {
                    if ("patch".equals(xr.getLocalName())) {
                        Patch p = xm.readValue(xr, Patch.class);
                        XMLStreamReader xr1 = xif.createXMLStreamReader(new FileInputStream(homeLocation + "/inventory/patches/"+p.getId()+".xml"));
                        while (xr1.hasNext()) {
                            xr1.next();
                            if (xr1.getEventType() == START_ELEMENT) {
                                if ("patch-def".equals(xr1.getLocalName())) {
                                    PatchDef pd = xm.readValue(xr1, PatchDef.class);
                                    oneoffs.add(OneOff.builder().id(pd.getId()).description(pd.getDescription()).build());
                                }
                            }
                        }
                        xr1.close();
                    }
                }
            }
            xr.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            XMLStreamReader xr = xif.createXMLStreamReader(new FileInputStream(homeLocation + "/inventory/ContentsXML/oraclehomeproperties.xml"));
            while (xr.hasNext()) {
                xr.next();
                if (xr.getEventType() == START_ELEMENT) {
                    if ("PROPERTY".equals(xr.getLocalName())) {
                        HomeProperty p = xm.readValue(xr, HomeProperty.class);
                        if (p.getName().equals("ORACLE_BASE")) {
                            homeBase=p.getValue();
                        }
                    }
                }
            }
            xr.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        if (comps.containsKey("oracle.crs")) {
            homeType = "CRS";
            homeRelease = comps.get("oracle.crs").getBaseVersion();
        } else if (comps.containsKey("oracle.server")) {
            homeType = "RDBMS";
            homeRelease = comps.get("oracle.server").getBaseVersion();
        } else if (comps.containsKey("oracle.sysman.top.oms")) {
            homeType = "OMS";
            homeRelease = comps.get("oracle.sysman.top.oms").getBaseVersion();
        } else if (comps.containsKey("oracle.sysman.top.agent")) {
            homeType = "AGENT";
            homeRelease = comps.get("oracle.sysman.top.agent").getBaseVersion();
        } else if (comps.containsKey("oracle.client")) {
            homeType = "CLIENT";
            homeRelease = comps.get("oracle.client").getBaseVersion();
        }

        processOneOffs(oneoffs);

    }

    public void processOneOffs(Set<OneOff> oneoffs) {
        for (OneOff oneoff : oneoffs) {
            log.info("Processing oneoff {}", oneoff.getDescription());
            if (homeRelease.equals("19.0.0.0.0")) {
                if (dbPsu19cRef.contains(oneoff.getId())) {
                    dbPSU.add(oneoff);
                }
            } else if (homeRelease.equals("13.5.0.0.0")) {
                if (agentPsu19cRef.contains(oneoff.getId())) {
                    dbPSU.add(oneoff);
                }
            }
        }
        try {
            String result=dbPSU.last().getDescription().replaceAll("[A-Za-z:()]","").trim();
            for (String version:result.split("\\s+")) {
                if (version.contains(".")) {
                    homeVersion=version;break;
                }
            }
        } catch (Exception e) {
            log.error("No psu found for {} ({})", homeLocation, homeRelease);
        }
        if (homeVersion==null) homeVersion=homeRelease;

    }

    public void fetchDbsHome() {
        if (Files.exists(Path.of(homeLocation+"/bin/orabaseconfig"))) {

        }
    }

    public boolean valid() {
        return isValid;
    }

    public void fetchToolsDetails() {
        homeType=tools.get(homeLocation);
    }

}