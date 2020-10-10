<?php

//////////////////////////////////////////////////////////
// Soheil Nazar Shahsavani
// nazarsha@usc.edu
// To run the code you should modify the location of 
// solr-php-client, the port number (default is 8983)
// and create a folder "url" to save the url of the queries
//////////////////////////////////////////////////////////



// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;

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

?>
<html>
  <head>
    <title>CSCI572 HW4</title>
  </head>
  <body>
    <form  accept-charset="utf-8" method="get">
      <label for="q">Search:</label>
      <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
      <input type="submit"/>
      <input type="checkbox" name="PageRank" value="yes" <?php if (isset ($_GET['PageRank'])) echo "checked" ;?> /> Use PageRank
	<BR><p style="font-weight:bold;"> <?php if ( isset($_GET['PageRank'])) echo 'Retrieved results using PageRank Alg.' ?> </p>
    </form>
<?php

// display results
if ($results)
{
  $total = (int) $results->response->numFound;
  $start = min(1, $total);
  $end = min($limit, $total);
?>
    <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
    <ol>
<?php
  // iterate result documents

  $out_file = 'urls/myURLs.txt';
  $out_handle = fopen ($out_file, 'a') or die ('cant open file: '. $out_file);
   $head = $_GET['q'];
   if (isset ($_GET['PageRank'])) $head .= "_pr";
   fwrite ($out_handle, $head.PHP_EOL);


  foreach ($results->response->docs as $doc)
  {
?>
      <li>
        <table style="border: 1px solid black; text-align: left">
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

	   //$temp = str_repeat ( $url, 1);
           //array_push ( $result_ar, $temp);
	   fwrite ($out_handle, $url.PHP_EOL);
           //<li><b> title </b>:  <a href=url>  dasd asd</a></li>
           if ( isset ($title) ) {

              if ( isset ($url) )     
     	      echo "<tr><td><b> title: </b> <a href='" . $url . "'>". $title . "</a></td></tr>"; 
              else 
     	      echo "<tr><td><b> title: </b>" . $title . "</a></td></tr>"; 

           }

           if ( isset ($url) ) 
     	      echo "<tr><td><b> Link: </b> <a href='" . $url . "'>". $url . "</a></td></tr>"; 
           if ( isset ($desc) ) 
     	      echo "<tr><td><b> Description: </b> " . $desc . "</td></tr>"; 	
           else 
     	      echo "<tr><td><b> Description: </b> NA </td></tr>"; 	

           if ( isset ($id) ) 
     	      echo "<tr><td><b> ID: </b> " . $id . "</td></tr>"; 
	  echo "<BR>";
 unset ($title); unset($url); unset($id); unset($desc);
  //var_dump ($result_ar);
?>
        </table>
      </li>
<?php
  }
  $fclose ($out_handle);
?>

    </ol>
<?php
}
?>
  </body>
</html>
