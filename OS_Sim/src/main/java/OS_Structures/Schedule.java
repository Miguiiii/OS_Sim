/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package OS_Structures;

/**
 *
 * @author ile2
 */
public enum Schedule {
    PRIORITY{
        @Override
        public String toString() {
            return "Priority";
        }
    },
    FIFO{
        @Override
        public String toString() {
            return "First In First Out";
        }
    },
    ROUND_ROBIN{
        @Override
        public String toString() {
            return "Round Robin";
        }
    },
    SHORTEST_NEXT{
        @Override
        public String toString() {
            return "Shortest Process Next";
        }
    },
    SHORTEST_REMAINING_TIME{
        @Override
        public String toString() {
            return "Shortest Remaining Time Next";
        }
    },
    HIGHEST_RESPONSE_RATIO{
        @Override
        public String toString() {
            return "Highest Response Ratio";
        }
    },
    FEEDBACK{
        @Override
        public String toString() {
            return "Feedback";
        }
    };
}
