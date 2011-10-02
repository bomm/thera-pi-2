function doHighlight(bodyText, searchTerm, highlightStartTag, highlightEndTag,vg,hg)
{
  // the highlightStartTag and highlightEndTag parameters are optional
  if ((!highlightStartTag) || (!highlightEndTag)) {

   //highlightStartTag = "<font style='color:blue; background-color:yellow;'>";
   highlightStartTag = "<font style='color:"+vg+"; background-color:"+hg+";'>";
   highlightEndTag = "</font>";
  }

  var newText = "";
  var i = -1;
  var lcSearchTerm = searchTerm.toLowerCase();
  var lcBodyText = bodyText.toLowerCase();

  while (bodyText.length > 0) {
    i = lcBodyText.indexOf(lcSearchTerm, i+1);
    if (i < 0) {
      newText += bodyText;
      bodyText = "";
    } else {
      // skip anything inside an HTML tag
      if (bodyText.lastIndexOf(">", i) >= bodyText.lastIndexOf("<", i)) {
        // skip anything inside a <script> block
        if (lcBodyText.lastIndexOf("/script>", i) >= lcBodyText.lastIndexOf("<script", i)) {
          newText += bodyText.substring(0, i) + highlightStartTag + bodyText.substr(i, searchTerm.length) + highlightEndTag;
          bodyText = bodyText.substr(i + searchTerm.length);
          lcBodyText = bodyText.toLowerCase();
          i = -1;
        }
      }
    }
  }

  return newText;
}


function highlightSearchTerms(searchText, treatAsPhrase, warnOnFailure, highlightStartTag, highlightEndTag)
{
  if (treatAsPhrase) {
    searchArray = [searchText];
  } else {
    searchArray = searchText.split(" ");
  }

  if (!document.body || typeof(document.body.innerHTML) == "undefined") {
    if (warnOnFailure) {
      alert("Sorry, for some reason the text of this page is unavailable. Searching will not work.");
    }
    return false;
  }

var bodyText = document.body.innerHTML;
var Hintergund  = new Array("#FFF740", "#00FFFF" ,"#FF0000", "#DF64BD" );
var Schrift  = new Array( "#0000FF" ,"#FF0000" , "#FFFFFF", "#FFF740" );
  var vg = "#FF0000";
  var hg = "#0000FF";
  for (var i = 0; i < searchArray.length; i++) {
    if(i < 4){
      vg = Schrift[i];
      hg = Hintergund[i];
    }else{
      vg = "#FFFFFF";
      hg = "#000000";
    }
    bodyText = doHighlight(bodyText, searchArray[i], highlightStartTag, highlightEndTag,vg,hg);
  }

  document.body.innerHTML = bodyText;
  return true;
}

function searchPrompt(defaultText, treatAsPhrase, textColor, bgColor)
{

  if (!defaultText) {
    defaultText = "";
  }

  // we can optionally use our own highlight tag values
  if ((!textColor) || (!bgColor)) {
    highlightStartTag = "";
    highlightEndTag = "";
  } else {
    highlightStartTag = "<font style='color:" + textColor + "; background-color:" + bgColor + ";'>";
    highlightEndTag = "</font>";
  }

  if (treatAsPhrase) {
    promptText = "Please enter the phrase you'd like to search for:";
  } else {
    promptText = "Please enter the words you'd like to search for, separated by spaces:";
  }

  searchText = prompt(promptText, defaultText);

  if (!searchText)  {
    alert("No search terms were entered. Exiting function.");
    return false;
  }

  return highlightSearchTerms(searchText, treatAsPhrase, true, highlightStartTag, highlightEndTag);
}
