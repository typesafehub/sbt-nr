package sbt.nr

import org.junit.{ Test, Rule }
import org.junit.rules.ExpectedException
import org.hamcrest.core.StringContains

class SbtNrPluginTest {

  val expectedException = ExpectedException.none()
  @Rule def ExpectedExceptionDef = expectedException

  val currentClassPath = classOf[SbtNrPluginTest].getProtectionDomain.getCodeSource.getLocation.getPath

  @Test
  def verifyExceptionForMissingJarFileValue() {
    expectedException.expect(classOf[RuntimeException])
    val errorMessage = "Set 'newRelicAgentJar in NewRelic := \"<filename>\"'"
    expectedException.expectMessage(new StringContains(errorMessage))
    SbtNrPlugin.verifySettings("", "SomeConfigFileValue")
  }

  @Test
  def verifyExceptionForMissingConfigFileValue() {
    expectedException.expect(classOf[RuntimeException])
    val errorMessage = "Create a configuration file"
    expectedException.expectMessage(new StringContains(errorMessage))
    SbtNrPlugin.verifySettings("SomeJarFileValue", "")
  }

  @Test
  def verifyExceptionsForMissingValues() {
    expectedException.expect(classOf[RuntimeException])
    val first = "Set 'newRelicAgentJar in NewRelic := \"<filename>\"'"
    val second = "Create a configuration file"
    expectedException.expectMessage(new StringContains(first))
    expectedException.expectMessage(new StringContains(second))
    SbtNrPlugin.verifySettings("", "")
  }

  @Test
  def verifyExceptionsForMissingJarFile() {
    expectedException.expect(classOf[RuntimeException])
    val errorMessage = "The specified file 'SomeJarFileValue' does not exist."
    expectedException.expectMessage(new StringContains(errorMessage))
    SbtNrPlugin.verifySettings("SomeJarFileValue", currentClassPath)
  }

  @Test
  def verifyExceptionsForMissingConfigFile() {
    expectedException.expect(classOf[RuntimeException])
    val errorMessage = "The specified file 'SomeConfigFileValue' does not exist."
    expectedException.expectMessage(new StringContains(errorMessage))
    SbtNrPlugin.verifySettings(currentClassPath, "SomeConfigFileValue")
  }

  @Test
  def verifyExceptionsForMissingFiles() {
    expectedException.expect(classOf[RuntimeException])
    val first = "The specified file 'SomeJarFileValue' does not exist."
    val second = "The specified file 'SomeConfigFileValue' does not exist."
    expectedException.expectMessage(new StringContains(first))
    expectedException.expectMessage(new StringContains(second))
    SbtNrPlugin.verifySettings("SomeJarFileValue", "SomeConfigFileValue")
  }

  @Test
  def verifyNoException() {
    SbtNrPlugin.verifySettings(currentClassPath, currentClassPath)
  }
}