<?php

// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

ini_set ('memory_limit', '-1');

$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;
include 'SpellCorrector.php';
?>


<html>
  <head>

<link rel="stylesheet" href="style.css"/>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">


function completeInput(myTxt) {

$("#inputBox").val(myTxt);
$("#suggestions ul").html('');
$("#suggestions").css({'display': 'none'});

}


function callAPI () {
    var txt = $("#inputBox").val().trim().toLowerCase();
    var txtVals = txt.split(" ");
    txt = txtVals[txtVals.length - 1]; 
    var rem = txtVals.slice (0, -1).join(" ");
    //txt  = encodeURIComponent (txt);
    var url = "http://localhost:8983/solr/myexample/suggest";
    console.log (txt)
    if (txt.length == 0){
      $("#suggestions ul").html('');
      $("#suggestions").css({'display': 'none'});
      return;
     }

    //$.get (url, function (data, stat) {
     //   alert ('Data: ', data, 'Stat: ', stat);
	//}

    $.ajax ({
       url: url,
       data: {q:txt},
       dataType : 'jsonp',
       crossdomain: true,
       jsonp: 'json.wrf',
	success: function (data, stat, xhr) {
            //console.log ('Data: ', data);
            sugs = []
            try {
		s = data.suggest.suggest
                for (var d in s){
                  if (s.hasOwnProperty(d)){
		     for (var p in s[d].suggestions){
		       var ret = s[d].suggestions[p];
		       //var res = ret.substring(0,1).toUpperCase(); 
		       //res += ret.substring(1);
                       sugs.push(ret);
		     }
		  }
		}
                console.log ('Suggs:', sugs);
	    } 
            catch (e) {
               console.log (' Couldnt get the suggestions', e);
	    }
            
	    //adding suggestions to html
             $("#suggestions ul").html('');
	    for (var d in sugs){
	      //var link = "http://localhost/search.php?q=" + sugs[d].term.trim() ;
              //var litem = "<li><a href='" + link + "'> <span> " + sugs[d].term  + "   </span> </a></li>";
	      var tt = sugs[d].term;
	      for (var i=0; i < tt.length; i++){
		if (tt.charAt(i) == "\'")
		   tt = tt.substr(0,i-1) +  "\’" + tt.substr(i+1);
                else if (tt.charAt(i) == '\"')
		   tt = tt.substr(0,i-1) +  "\’" + tt.substr(i+1);
		}

              var litem = "<li onclick=' completeInput(\"" + rem + ' ' + tt + "\")'>  <span> " + rem + ' ' + tt  + "   </span> </a></li>"

	      $("#suggestions ul").append(litem);

	    } 
             var bBox = $('#inputBox')[0].getBoundingClientRect();              
             var left = bBox.left + "px";
             var top = bBox.top + bBox.height + window.scrollY;
             top += "px";
             var wdth = bBox.width + "px";
             $("#suggestions").css({'display': 'block', 'top': top, 'left': left, 'minWidth': wdth});
              


	}
	});

}






</script>

    <title>CSCI572 HW4</title>
  </head>
  <body>
    <form  accept-charset="utf-8" method="get">
      <label for="q">Search:</label>
      <input id="inputBox" name="q" type="text" autocomplete="off" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"   oninput= "callAPI();" />
      <input type="submit"/>
      <input type="checkbox" name="PageRank" id="PageRank" value="yes" <?php if (isset ($_GET['PageRank'])) echo "checked" ;?> /> Use PageRank
	<BR><p style="font-weight:bold;"> <?php if ( isset($_GET['PageRank'])) echo 'Retrieved results using PageRank Alg.' ?> </p>
    </form>

<div id="suggestions">
<ul >

</ul>
</div>


<?php


if ($query)
{
  // The Apache Solr Client library should be on the include path
  // which is usually most easily accomplished by placing in the
  // same directory as this script ( . or current directory is a default
  // php include path entry in the php.ini)
  require_once('Apache/Solr/Service.php');

  // create a new solr service instance - host, port, and webapp
  // path (all defaults in this example)
  $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample');


  $err = 0;
  $correctedWords = array();
  $inputTerms = explode (' ', $query);
  $correctedQuery = array();

  foreach ( $inputTerms as $key => $val) {
    $correct = SpellCorrector::correct($val);
    if ( strcasecmp($correct, $val)  ){ 
       $correctedWords[$key] = "<b><em>". $correct . "</em></b>";
       $correctedQuery[$key]= $correct;
       $err ++ ;
    } 
    else {
       $correctedWords[$key] = $correct;
       $correctedQuery[$key]= $correct;
    }

  } 
   

  if ($err != 0 && !isset ( $_GET['showExact']) ) {
     $corr =  implode (' ', $correctedWords);
     $correctedLink  = implode (' ', $correctedQuery);
     $corr = "<a href='http://localhost/search.php?q=$correctedLink'>$corr</a>";
      $fal = $query;
     echo "<p> Showing results for $corr </p>";     
     echo "<p> Search instead for <a href='http://localhost/search.php?q=$fal&showExact=1'";
     echo "<span>$fal</span></a></p>";
   $query = implode (' ', $correctedQuery );
   }



  // if magic quotes is enabled then stripslashes will be needed
  if (get_magic_quotes_gpc() == 1)
  {
    $query = stripslashes($query);
  }

  // in production code you'll always want to use a try /catch for any
  // possible exceptions emitted  by searching (i.e. connection
  // problems or a query parsing error)
  try
  {
    $extraArg = array();
    if (isset ($_GET["PageRank"])){
	$extraArg = array ('sort' => 'pageRankFile desc');
    } 
	$results = $solr->search($query, 0, $limit, $extraArg); 
     
  }
  catch (Exception $e)
  {
    // in production you'd probably log or email this error to an admin
    // and then show a special message to the user but for this example
    // we're going to show the full exception
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}


// display results
if ($results)
{
  $total = (int) $results->response->numFound;
  $start = min(1, $total);
  $end = min($limit, $total);
?>
    <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
    <ul style="list-style-type: none;">
<?php
  // iterate result documents

  //$out_file = 'urls/myURLs.txt';
  //$out_handle = fopen ($out_file, 'a') or die ('cant open file: '. $out_file);
   $head = $_GET['q'];
   if (isset ($_GET['PageRank'])) $head .= "_pr";
   //fwrite ($out_handle, $head.PHP_EOL);


   


  foreach ($results->response->docs as $doc)
  {
?>
      <li>
        <!-- <table style="border: 1px solid black; text-align: left"> -->
	<table style="text-align: left">
<?php
    // iterate document fields / values
    $search_array = array ('title', 'id','og_url', 'og_description');
    $result_ar = array();




    foreach ($doc as $field => $value)
    {

          //$title = ''; $id = ''; $url = ''; $desc = '';
	  if ( in_array ( $field,  $search_array) ){
          
	   $title = ( $field == 'title') ? $value : $title;
	   $id = ( $field == 'id') ? $value : $id;
           $url = ( $field == 'og_url') ? $value : $url;
	   $desc = ( $field == 'og_description') ? $value : $desc; 
           }


     }      
           if (! isset ($url) ) {
              $file = fopen ('/home/soheil/Downloads/NBC_News/UrlToHtml_NBCNews.csv', 'r');
              $chunks = explode ("/", $id);
              $myId = end($chunks);


              while (! feof ($file) ){
                 $row = fgetcsv ($file);
                 //var_dump ($row);
                 if ( $row[0] == $myId ){
                    $url = $row[1];
                    //echo "found url";
                    break;
                 }
	       }
              fclose ($file);
              //echo $myId . ": ". $url;

	   }

	   //fwrite ($out_handle, $id.PHP_EOL);
           //<li><b> title </b>:  <a href=url>  dasd asd</a></li>
           if ( isset ($title) ) {

              if ( isset ($url) )     
     	      echo "<tr><td><a href='" . $url . "'>". $title . "</a></td></tr>"; 
              else 
     	      echo "<tr><td>" . $title . "</a></td></tr>"; 

           }

           if ( isset ($url) ) 
     	      echo "<tr><td ><p class='link_class' >". $url . "</p></td></tr>"; 
           //if ( isset ($desc) ) 
     	   //   echo "<tr><td> " . $desc . "</td></tr>"; 	
           //else 
     	   //   echo "<tr><td> NA </td></tr>"; 	

           //if ( isset ($id) ) 
     	   //   echo "<tr><td> " . $id . "</td></tr>"; 

           //Building snippets
           $fileName = explode ('/', $id);
           $fileName = end($fileName);
           $fileName = '/home/soheil/Downloads/NBC_News/HTML_files/' . $fileName ;
           $fileContent =  str_replace("\u003c", "<", str_replace( '&#39;', "\'", htmlspecialchars_decode (file_get_contents($fileName),ENT_QUOTES))); 
           $text = preg_replace("/<script[^<]*<\/script>/", "\n", $fileContent);	   
           $sentences = preg_split("/(\n+|\.\s+|\"\s*\")/", $text);
           $queryWords = explode(' ', trim($query));
           //echo count ( $sentences );
           //var_dump($sentences);
           $snippet = array();
           $usedSentences = array();

           foreach ($sentences as $curSentKey => $curSentVal) {
             foreach ($queryWords as $curWordKey => $curWordVal) {
               $curTokens = explode (' ', $curSentVal);
               foreach ($curTokens as $tokenKey => $tokenVal ){
                 if (strcasecmp($tokenVal, $curWordVal) == 0 ){
                    if (array_search ($curSentKey,$usedSentences) != FALSE) 
                      break;
                      array_push( $snippet, "..."); 
                      foreach ($curTokens as $tokKey => $tok){
                        $tokPushed = False;
                        foreach ($queryWords as $qw){
                           if (strcasecmp($tok, $qw) == 0 ){
                              array_push( $snippet, "<b>". trim($tok)."</b>");
                               $tokPushed = True; 
                            }                           
                        }
                       if ($tokPushed == False){
                              array_push( $snippet, trim($tok) );
			}
                      }

                    array_push ( $usedSentences, $curSentKey);
                    break;
                  }
               }
             }
           }

          $snippet = array_splice($snippet, 1);
          //removing first sentence
          $pos = array_search ('...', $snippet);
          $snippet = array_splice($snippet, $pos+1);
          //echo  $pos ;
          $snippet = implode ( ' ', $snippet) ;
          if ( strlen ($snippet) > 300 ) 
              $snippet = substr ($snippet, 0, 160).' ...';
          $snippet = "<tr><td >". $snippet . "</td></tr>" ; 
          echo $snippet;









	  echo "<BR>";
 unset ($title); unset($url); unset($id); unset($desc); unset ($snippet); unset($usedSentences);
?>
        </table>
      </li>
<?php
  }
?>

    </ul>
<?php
}
?>

  </body>
</html>
