import com.jcvb.UltimateBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UltimateBoardTests {
	
	@Test
	public void testGetBoard() {
		UltimateBoard ultimateBoard = new UltimateBoard();
		
		for (int i = 0; i < 9; i++) {
			System.out.println(i);
			Assertions.assertEquals("___ ___ ___", ultimateBoard.getBoard(i).toString());
		}
	}
	
	@Test
	public void testGetPossibleMoves(){
		UltimateBoard ultimateBoard = new UltimateBoard();
		List<Integer> moves = ultimateBoard.getPossibleMoves();
		
		// Empty board should have 81 possible moves as the board has 81 position
		Assertions.assertEquals(81, ultimateBoard.getPossibleMoves().size());
		
		// make all moves
		for(int move: moves){
			ultimateBoard.makeMove(move);
		}
		
		// Full board should have no possible moves left
		Assertions.assertEquals(0, ultimateBoard.getPossibleMoves().size());
	}
}
