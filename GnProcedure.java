package net.mcreator.enr.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.level.BlockEvent;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;
import java.util.Scanner;


@Mod.EventBusSubscriber
public class GnProcedure {
	public static ArrayList<Double[]> blocs = new ArrayList<Double[]>();

	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player && !_player.level.isClientSide() && world.getBlockState(new BlockPos(x, y, z)) ==  Blocks.EXPOSED_CUT_COPPER.defaultBlockState()){
			_player.displayClientMessage(Component.literal("rien"), (false));

			try{

				File myObj = new File("file.json");
				Scanner myReader = new Scanner(myObj);
				new Thread(() -> generate(event, world, x, y, z, entity, myReader)).start();

			}

			catch(Exception e) {
					_player.displayClientMessage(Component.literal("erreur "+ e), (false));
			}
		}
	}

	private static void generate(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Scanner reader){
		try{
				if (reader.hasNextLine()) {

						for(Double[] coord : blocs){

								world.setBlock(new BlockPos(coord[0], coord[1], coord[2]), Blocks.AIR.defaultBlockState(), 3);

						}

						blocs = new ArrayList<Double[]>();

						String data = reader.nextLine();
						String[] parts = data.split(" ");
						for(String block : parts){
								String[] donneeString = block.split("_");
								Double[] donnee = {Double.parseDouble(donneeString[0])+x, Double.parseDouble(donneeString[1])+y, Double.parseDouble(donneeString[2])+z};
								//_player.displayClientMessage(Component.literal(String.valueOf(Double.parseDouble(donnee[1])+x)) + ", " + String.valueOf(Double.parseDouble(donnee[1])+x)), (false));
								BlockPos bloc = new BlockPos(donnee[0], donnee[1], donnee[2]);
								blocs.add(donnee);
								world.setBlock(bloc, Blocks.EXPOSED_CUT_COPPER.defaultBlockState(), 3);

						}
						TimeUnit.MILLISECONDS .sleep(200);
						new Thread(() -> generate(event, world, x, y, z, entity, reader)).start();

				}
				else{
					File myObj = new File("file.json");
					Scanner myReader = new Scanner(myObj);
					new Thread(() -> generate(event, world, x, y, z, entity, myReader)).start();
				}
		}
		catch(Exception e) {
		}

	}

}
