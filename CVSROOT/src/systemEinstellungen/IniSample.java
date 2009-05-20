package systemEinstellungen;
/*
 * Copyright 2005 [ini4j] Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


public class IniSample
{
    public static void main(String[] args) throws Exception
    {
        String filename = args.length > 0 ? args[0] : "c:\reha.ini";
        INIFile ini = new INIFile("c:\\reha.ini");
        String kollegen =  ini.getStringProperty("Preferences","FeldSet1");
        String[] kolls = kollegen.split(",");
        System.out.println(kolls.length);
        System.out.println(kollegen);
        ini.setStringProperty("Preferences","FeldSet13", "Doofilein2","Preferenz = Doofilein");
        ini.save();
        
    }
}