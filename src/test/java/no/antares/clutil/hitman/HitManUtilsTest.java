package no.antares.clutil.hitman;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class HitManUtilsTest {

	@Test public void test() {
		List<String> result	= HitManUtils.options( "java -jar FtpApp.jar -msg \"Queue1 Queue2\" -url localhost:1099" );
		assertThat( result.size(), is( 7 ) );
		assertThat( result, hasItems( "java", "-jar", "FtpApp.jar", "-msg", "Queue1 Queue2", "-url", "localhost:1099" ) );
	}

}
