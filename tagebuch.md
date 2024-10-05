# Praktikumstagebuch
### Caspar von Beöczy, Nick Schirm, Niklas Alfringhaus

## Themenbeschreibung
### Ultimate TicTacToe
Wir haben es uns zur Aufgabe gemacht, dieses Spiel mit einem MiniMax-Algorithmus 
mit Alpha-Beta-Pruning und selbst entwickelter Heuristik zu untersuchen. Das Spiel umfasst 9
einzelne TicTacToe Felder, die in einem großen Feld angeordnet sind. Die Auswahl des zu spielenden
Feldes wird bestimmt durch die Platzierung des Markers des vorher Spielenden.

## Umsetzung
Da durch die höhere Komplexität des Spieles weitaus mehr Zustände möglich sind (3^81) als im 
Basis-Spiel, wird die Implementierung der 9 TicTacToe Felder über BitBoards realisiert, 
ähnlich wie es für Schach genutzt wird. MiniMax wird zusammen mit Alpha-Beta-Pruning genutzt, 
um den Spielbaum zu verkleinern, bzw. mögliche Zustände zu minimieren. Die gesamte Implementierung
wird in Java umgesetzt.

## Heuristik
* Durch die erweiterten Regeln in diesem Spiel gibt es die Möglichkeit, dass ein Ungleichgewicht der 
platzierten Markierungen der Spieler >1 herrscht, wird ein einzelnes Board, in dem die Differenz dieser Markierungen 
positiv für den Spieler ist bewertet. Durch diesen Vorteil erwarten wir, dass MiniMax dadurch sichere Wege findet, 
die Gewinnchance zu maximieren.
* Zum anderen wird innerhalb eines Feldes bestimmt, wie viele Gewinnmöglichkeiten bestehen. Das wird zudem
auf der großen Ebene des gesamten Ultimate Bordes getestet.
* Dazu wird für den Spieler ein Zug begünstigt, bei dem er ein TTT-Bord gewinnt.
* Außerdem wird berücksichtigt, ob mit dem Zug das Ultimate TTT Spiel gewonnen wird

## Experimente
* Zum einen soll die Erfolgschance unserer Heuristik getestet werden. Dazu wird ein RandomPlayer implementiert,
welcher zufällige Züge macht. Dieser „spielt“ in 100 simulierten Spielen gegen den Bot, welcher den 
MiniMax-Algorithmus verwendet. Unser Ziel dabei ist, dass der Bot in mindestens 80 % aller Fälle gewinnt. Dabei beginnen
abwechselnd die beiden Spieler um einen Durchschnitt zu bestimmen.
* Dies wird für die Tiefen 1-8 getestet, um den Verlauf der Gewinnchance darzustellen.

## Ergebnisse

| Tiefe | #MiniMax | #RandomPlayer | #Unentschieden |
|-------|----------|---------------|----------------|
| 1     | 70       | 15            | 15             | 
| 2     | 76       | 13            | 11             |
| 3     | 77       | 11            | 12             |
| 4     | 83       | 6             | 11             |
| 5     | 79       | 10            | 11             |
| 6     | 86       | 6             | 8              |
| 7     | 75       | 10            | 15             |
| 8     | 83       | 10            | 7              |

## Fazit
Das gesetzte Ziel von einer 80 % Gewinnwahrscheinlichkeit gegen den RandomPlayer wurde in mehreren Testfällen erreicht.

Während der Entwicklung haben wir festgestellt, dass die Implementierung eines solchen Algorithmus sehr fehleranfällig ist. 
Mehrere Male haben kleine Fehler im Code oder Verständnis dazu geführt, dass der Algorithmus nicht wie erwartet funktioniert hat.
Durch Nicht-Erkennen der Fehlerquelle haben wir unsere Zeit erst genutzt, um eine bessere Heuristik zu entwickeln, obwohl 
diese letztendlich nicht zur Verbesserung der Ergebnisse geführt hat.
Durch diese Fehler fiel die Gewinnwahrscheinlichkeit des Bots in manchen Fällen unter 30 %. Dies war durch die Verwechslung 
der Algorithmen MiniMax und NegaMax zurückzuführen, da die Heuristik unterschiedlich implementiert werden muss.

Wichtig war die interne Speicherung des Bordes als Bitboard, was zwar komplex in der Planung ist, jedoch sehr viel Rechenzeit erspart.
Wir haben außerdem festgestellt, wie groß der Unterschied zwischen Standard-Funktionen wie Arrays.stream() und einer einfachen Schleife ist.
Der HumanPlayer hat uns dabei geholfen, die Implementierung zu testen und zu debuggen und Entscheidungen für die Heuristik 
zu treffen. Man erkennt mit dem manuellen Spiel falsche Muster, die MiniMax wiederholt nutzt.

Eine Herausforderung war es, dass das von uns gewählte Spiel sehr komplex ist und es schwierig ist, den Algorithmus zu testen.
Durch die starke Verzweigung des Spielbaums ist es schwierig, den Algorithmus zu testen, da die Anzahl der möglichen Zustände sehr groß ist.
Auch das Durchführen der Experimente war schwierig, da ein Durchlauf mit hoher Tiefe mehrere Minuten gedauert hat.

## Quellen
- KI_02 Zwei-Personen-Nullsummenspiele - MiniMax.pdf 
- [MiniMax und Alpha-Beta Pruning](https://www.youtube.com/watch?v=l-hh51ncgDI)
- [Flipping-Mirroring-Rotating von Bitboards](https://www.chessprogramming.org/Flipping_Mirroring_and_Rotating)
- [Bit-Shift-Rotation](https://x-engineer.org/bit-shift-rotation-scilab/)
- [Zählen von gesetzten Bits in einem Integer](https://tekpool.wordpress.com/category/bit-count/)
- [Unsigned Byte zu Integer casten](https://stackoverflow.com/questions/7401550/how-to-convert-int-to-unsigned-byte-and-back/7401635#7401635)