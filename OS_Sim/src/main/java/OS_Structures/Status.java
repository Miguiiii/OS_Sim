/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package OS_Structures;

/**
 *
 * @author ile2
 */
public enum Status {
    NEW{
        @Override
        public String toString() {
            return "New";
        }
    },
    RUNNING{
        @Override
        public String toString() {
            return "Running";
        }
    },
    READY{
        @Override
        public String toString() {
            return "Ready";
        }
    },
    BLOCKED{
        @Override
        public String toString() {
            return "Blocked";
        }
    },
    BLOCKED_SUSPENDED{
        @Override
        public String toString() {
            return "Blocked/Suspended";
        }
    },
    READY_SUSPENDED{
        @Override
        public String toString() {
            return "Ready/Suspended";
        }
    },
    EXIT{
        @Override
        public String toString() {
            return "Exit";
        }
    };
}
