import com.jcvb.Bitboard;
import com.jcvb.GameStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BitboardTests {
	
	@Test
	public void testSet() {
		Bitboard bitboard = Bitboard.fromString("X__ ___ ___");
		
		Assertions.assertEquals("X__ ___ ___", bitboard.toString());
		
		bitboard.set("__X ___ ___");
		
		Assertions.assertEquals("X_X ___ ___", bitboard.toString());
		
		bitboard.set("__X ___ _X_");
		
		Assertions.assertEquals("X_X ___ _X_", bitboard.toString());
		
		bitboard.set("_O_ ___ ___");
		
		Assertions.assertEquals("XOX ___ _X_", bitboard.toString());
		
		System.out.println(bitboard);
	}
	
	@Test
	public void testGetPossibleMoves() {
		Bitboard bitboard = Bitboard.fromString("XOX XOX X__");
		System.out.println(bitboard);
		
		Assertions.assertEquals(2, bitboard.getPossibleMoves(new ArrayList<>()).size());
	}
	
	@Test
	public void testGetPossibleMovesWithoutSymmetries() {
		Bitboard bitboard = Bitboard.fromString("X__ _X_ ___");
		System.out.println(bitboard);
		
		List<Integer> moves = bitboard.getPossibleMovesWithoutSymmetries(1);
		
		Assertions.assertEquals(6, moves.size());
	}
	
	@Test
	public void testCheckIfWon() {
		Bitboard bitboard = Bitboard.fromString("XOX XOX X__");
		Assertions.assertEquals(GameStatus.ONE, bitboard.checkIfWon());
		
		bitboard = Bitboard.fromString("XOX XOX ___");
		Assertions.assertEquals(GameStatus.RUNNING, bitboard.checkIfWon());
		
		bitboard = Bitboard.fromString("X__ _X_ __X");
		Assertions.assertEquals(GameStatus.ONE, bitboard.checkIfWon());
	}
	
	@Test
	public void testRotateRight() {
		Bitboard bitboard = Bitboard.fromString("___ __X ___");
		bitboard.rotateRight(1);
		
		Assertions.assertEquals("___ ___ _X_", bitboard.toString());
		
		System.out.println(bitboard);
		
		bitboard = Bitboard.fromString("___ __X ___");
		for (int i = 0; i < 4; i++) {
			bitboard.rotateRight(1);
			System.out.println(bitboard);
		}
	}
	
	@Test
	public void testFlipHorizontally() {
		Bitboard bitboard = Bitboard.fromString("___ ___ __X");
		bitboard.flipHorizontally();
		
		Assertions.assertEquals("__X ___ ___", bitboard.toString());
		
		bitboard.set("___ ___ X__");
		System.out.println(bitboard);
		bitboard.flipHorizontally();
		
		Assertions.assertEquals("X__ ___ __X", bitboard.toString());
		
		System.out.println(bitboard);
	}
	
	@Test
	public void testFlipVertically() {
		Bitboard bitboard = Bitboard.fromString("___ ___ __X");
		bitboard.flipVertically();
		
		Assertions.assertEquals("___ ___ X__", bitboard.toString());
	}
	
	@Test
	public void testTranslation() {
		for (int i = 0; i < 9; i++) {
			int bit = Bitboard.fromHumanToBit(i);
			int human = Bitboard.fromBitToHuman(bit);
			
			Assertions.assertEquals(i, human);
		}
	}
	
	@Test
	public void testFromString() {
		Bitboard bitboard = Bitboard.fromString("_X_ OXO _X_");
		
		Assertions.assertEquals(0b100100010, bitboard.extractChar(0));
		Assertions.assertEquals(0b10001000, bitboard.extractChar(1));
		
		System.out.println(bitboard);
	}
}
