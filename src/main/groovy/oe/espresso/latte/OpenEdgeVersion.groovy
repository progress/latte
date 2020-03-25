package oe.espresso.latte

import oe.espresso.latte.LatteExtension

class OpenEdgeVersion {

    String major

    String minor

    String revision

    String patchLevel

    String bitness

    String full

    String reduced

    String rcode

    public String toString() {
        return full
    }

}