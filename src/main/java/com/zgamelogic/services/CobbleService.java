package com.zgamelogic.services;

import com.zgamelogic.data.CobbleServiceException;
import com.zgamelogic.data.building.Building;
import com.zgamelogic.data.building.BuildingRepository;
import com.zgamelogic.data.enums.BuildingType;
import com.zgamelogic.data.enums.ResourceType;
import com.zgamelogic.data.history.HistoryRepository;
import com.zgamelogic.data.npc.Npc;
import com.zgamelogic.data.npc.NpcRepository;
import com.zgamelogic.data.player.Player;
import com.zgamelogic.data.player.PlayerRepository;
import com.zgamelogic.data.production.Production;
import com.zgamelogic.data.production.ProductionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zgamelogic.data.enums.BuildingType.*;

@Service
@Slf4j
@AllArgsConstructor
public class CobbleService {
    private final PlayerRepository playerRepository;
    private final HistoryRepository historyRepository;
    private final NpcRepository npcRepository;
    private final ProductionRepository productionRepository;
    private final BadNameService badNameService;
    private final ResourceService cobbleResourceService;
    private final BuildingRepository buildingRepository;

    @Scheduled(cron = "0 0 */12 * * *")
    public void day(){
        playerRepository.findAll().forEach(this::resolvePlayerDay);
        // TODO post day message
    }

    private void resolvePlayerDay(Player player){
        // TODO resolve building
        List<Building> unresolved = new ArrayList<>(player.getBuildings());
        for (int i = 0; i < player.getBuildings().size() && !unresolved.isEmpty(); i++) {
            for(Building building: unresolved.stream()
                .sorted(Comparator.comparingInt(building -> building.getProduction().getConsumption().size())).toList()){
                if(player.canAfford(building.getProduction().getConsumption())){
                    player.removeResources(building.getProduction().getConsumption());
                    player.addResources(building.getProduction().getProduction());
                    unresolved.remove(building);
                }
            }
        }


//        TODO resolve building/upgrading
//        TODO births
    }

    public Player startCobblePlayer(long playerId, String name) throws CobbleServiceException {
        if(playerRepository.existsById(playerId)) throw new CobbleServiceException(("A cobble town already exists for this player"));
        if(badNameService.isNotOkay(name)) throw new CobbleServiceException(("Town name is not okay"));
        Player player = new Player(playerId, name);
        UUID buildingUUID = UUID.randomUUID();
        player.addBuilding(TOWN_HALL, 1, "Town Hall", buildingUUID);
        player.addNpc(generateRandomCobbleNpc(player));
        player.addNpc(generateRandomCobbleNpc(player));
        player.getNpcs().get(0).setBuilding(player.getBuildings().get(0));
        player.addResource(ResourceType.WOOD, 5);
        return playerRepository.save(player);
    }

    public List<Npc> getCobbleNpcs(long playerId) throws CobbleServiceException {
        if(!playerRepository.existsById(playerId)) throw new CobbleServiceException("You must start the game first with the " + cobbleResourceService.cm("cobble start")  + " slash command");
        return npcRepository.findAllByPlayer_PlayerId(playerId);
    }

    public List<String> getCobbleBuildingList(){
        return Arrays.stream(values()).map(BuildingType::getFriendlyName).toList();
    }

    public Building getCobbleBuilding(long uid, String buildingId) throws CobbleServiceException {
        return buildingRepository.findByPlayer_PlayerIdAndBuildingId(uid, UUID.fromString(buildingId))
            .orElseThrow(() -> new CobbleServiceException(("No cobble building found")));
    }

    public void renameBuilding(Building building, String newName) throws CobbleServiceException {
        if(badNameService.isNotOkay(newName)) throw new CobbleServiceException(("Invalid building name"));
        building.setBuildingName(newName);
        buildingRepository.save(building);
    }

    public Player getCobblePlayer(long playerId) throws CobbleServiceException {
        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new CobbleServiceException(("No cobble player found. Have you started the game yet?")));
        return player;
    }

    public void renameTown(Player player, String newName) throws CobbleServiceException {
        if(badNameService.isNotOkay(newName)) throw new CobbleServiceException(("Invalid town name"));
        player.setTownName(newName);
        playerRepository.save(player);
    }

    public List<Production> getCobbleProductions(BuildingType buildingType){
        return productionRepository.findAllById_Building(buildingType);
    }

    private Npc generateRandomCobbleNpc(Player player) {
        Random rand = new Random();
        boolean male = rand.nextBoolean();
        String name = cobbleResourceService.randomName(male);
        String appearance = male ? "m" : "f";
        appearance += rand.nextInt(5); // skin color
        appearance += rand.nextInt(5); // hair color
        appearance += rand.nextInt(10); // hair style
        appearance += rand.nextInt(3); // eye color
        appearance += male ? rand.nextInt(5) : 0; // facial hair
        appearance += rand.nextInt(10); // shirt color
        appearance += rand.nextInt(3); // pant color
        return new Npc(player, name.split(" ")[0], name.split(" ")[1], appearance);
    }
}
