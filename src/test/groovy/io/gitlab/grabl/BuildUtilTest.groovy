package io.gitlab.grabl

import java.io.File
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification


class BuildUtilTest extends Specification {
    @Rule
    TemporaryFolder tmpDir = new TemporaryFolder()

    def "loadDbDeps returns map loaded from JSON cache"() {
        given: "an existing cache file"
        File cacheFile = tmpDir.newFile('grablDbDepCache.json')
        cacheFile.text = '{"db1":["prog1.p"],"db1;db2":["prog2.p","prog3.p"]}'

        expect: "loadDbDeps to return it's parsed contents"
        BuildUtil.loadDbDeps(cacheFile.path) == [
            'db1': ['prog1.p'],
            'db1;db2': ['prog2.p', 'prog3.p']
        ]
    }

    def "loadDbDeps returns an empty map when cache couldn't be loaded"() {
        expect: "loadDbDeps (when no existing cache) to return an empty map"
        BuildUtil.loadDbDeps() == [:]
    }
}
