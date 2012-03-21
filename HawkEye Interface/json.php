<?php
function json_decode ($json) 
  { 
      $json = str_replace(array("\\\\", "\\\""), array("&#92;", "&#34;"), $json); 
      $parts = preg_split("@(\"[^\"]*\")|([\[\]\{\},:])|\s@is", $json, -1, PREG_SPLIT_NO_EMPTY | PREG_SPLIT_DELIM_CAPTURE); 
      foreach ($parts as $index => $part) 
      { 
          if (strlen($part) == 1) 
          { 
              switch ($part) 
              { 
                  case "[": 
                  case "{": 
                      $parts[$index] = "array("; 
                      break; 
                  case "]": 
                  case "}": 
                      $parts[$index] = ")"; 
                      break; 
                  case ":": 
                    $parts[$index] = "=>"; 
                    break;    
                  case ",": 
                    break; 
                  default: 
                      return null; 
              } 
          } 
          else 
          { 
              if ((substr($part, 0, 1) != "\"") || (substr($part, -1, 1) != "\"")) 
              { 
                  return null; 
              } 
          } 
      } 
      $json = str_replace(array("&#92;", "&#34;", "$"), array("\\\\", "\\\"", "\\$"), implode("", $parts)); 
      return eval("return $json;"); 
  } 
function json_encode($in) { 
  $_escape = create_function('$str', 'return addcslashes($str, "\v\t\n\r\f\"\\/");');
  $out = ""; 
  if (is_object($in)) { 
    $class_vars = get_object_vars(($in)); 
    $arr = array(); 
    foreach ($class_vars as $key => $val) { 
      $arr[$key] = "\"{$_escape($key)}\":\"{$val}\""; 
    } 
    $val = implode(',', $arr); 
    $out .= "{{$val}}"; 
  }elseif (is_array($in)) { 
    $obj = false; 
    $arr = array(); 
    foreach($in AS $key => $val) { 
      if(!is_numeric($key)) { 
        $obj = true; 
      } 
      $arr[$key] = json_encode($val); 
    } 
    if($obj) { 
      foreach($arr AS $key => $val) { 
        $arr[$key] = "\"{$_escape($key)}\":{$val}"; 
      } 
      $val = implode(',', $arr); 
      $out .= "{{$val}}"; 
    }else { 
      $val = implode(',', $arr); 
      $out .= "[{$val}]"; 
    } 
  }elseif (is_bool($in)) { 
    $out .= $in ? 'true' : 'false'; 
  }elseif (is_null($in)) { 
    $out .= 'null'; 
  }elseif (is_string($in)) { 
    $out .= "\"{$_escape($in)}\""; 
  }else { 
    $out .= $in; 
  } 
  return "{$out}"; 
}	
?>