package imitatornet;

import java.io.File;
import java.util.*;

import puzzles.ChoiceAndExplanation;
import puzzles.PuzzleCreator;


import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import commonnetty.CommonBeginner;
import commonnetty.CommonDataListener;
import commonnetty.CommonSocketIOServer;
import commonnetty.LoggerWithStack;


/**
 * Listens to requests, and responds with imitator puzzles.
 *
 * @author Erel Segal the Levite
 * @since 2013-01-01
 */
public class ImitatorServer {

	/**
	 * Encapsulates a resource-edit request sent from another HTML form
	 */
	static class PuzzleRequest {
		public String corpus;
		public int numChoices;
	}

	/**
	 * Launch the server that listens to imitator requests
	 * @param args[0] = port number
	 */
	public static void main(String[] args) throws Exception {
		CommonBeginner.begin(args, "Imitator");

		final PuzzleCreator puzzleCreator = new PuzzleCreator(new File("../corpora").getAbsolutePath());
		final CommonSocketIOServer networkServer = new CommonSocketIOServer(args);
		final ChoiceAndExplanation[] defaultPuzzle = puzzleCreator.createPuzzle("ShmonaKvazim", 4);

		networkServer.addEventListener("createPuzzle", PuzzleRequest.class, new CommonDataListener<PuzzleRequest>(logger) {
			@Override public void onDataSub(SocketIOClient client, PuzzleRequest request, AckRequest ackRequest) throws Throwable {
				client.sendEvent("puzzle", defaultPuzzle);
				ChoiceAndExplanation[] puzzle = puzzleCreator.createPuzzle(request.corpus, request.numChoices);
				System.out.println(Arrays.asList(puzzle));
				client.sendEvent("puzzle", puzzle);
			}
		});
		networkServer.start();
	}

	private static String thisClassName = Thread.currentThread().getStackTrace()[1].getClassName();
	private static final LoggerWithStack logger = LoggerWithStack.getLogger(thisClassName);
	private static final LoggerWithStack clientlogger = LoggerWithStack.getLogger("clientlogger");
}
