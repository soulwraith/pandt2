<?php
	include 'firebaseInterface.php';
	include 'firebaseStub.php';
	include 'firebaseLib.php';
	include 'firebase_details.php';
	include 'facebook_helpers.php';
	
	function checkRoomAndUserValid($firebase, $toCheckRoom, $userId, $userToken, $winners, $losers){
		try {
			//-----------------check user token valid----------------
		   	$userValid = false;
			$dbToken = json_decode($firebase->get('secret/users/'.$userId.'/token'));
			$userValid = ($dbToken == $userToken);
			
			if(!$userValid) return false;
			
			
			
			//---------------------check user in room before-------------------
			$dbRoomJson = $firebase->get('rooms/'.$toCheckRoom->id);
			$dbRoom = json_decode($dbRoomJson);
			
			$haveUser = false;
			$loserFoundTeamSize = 0;
			$winnerFoundTeamSize = 0;
			if(!empty($dbRoom)){
				foreach ($dbRoom->originalRoomUsers as $key => $value){
					if($value->profile->userId == $userId){
						$haveUser = true;
						break;
					}
				}
				
				if(sizeof($losers) > 0){
					foreach($losers as $teamUserIdsString){
						if(checkTeamIsInRoom($teamUserIdsString, $dbRoom)) $loserFoundTeamSize++;
					}
				}
				
				foreach ($winners as $teamUserIdsString => $scoreDetailsArr) {
					if(checkTeamIsInRoom($teamUserIdsString, $dbRoom)) $winnerFoundTeamSize++;
				}
				
			}
			
			
			if(!$haveUser) return false;
			if(sizeof($losers) != $loserFoundTeamSize) return false;
			if(sizeof($winners) != $winnerFoundTeamSize) return false;
			
			//------------------------------check room details---------------------------
			$roomDetailsValid = false;
			if($dbRoom->roundCounter == $toCheckRoom->roundCounter && $dbRoom->roundCounter > 0){
				$roomDetailsValid = true;
			}
			
			if(!$roomDetailsValid) return false;
			//----------------------------------------------
			
			return true;
			
		} catch (Exception $e) {
		   	return false;
		}	
		
	}
	
	function checkTeamIsInRoom($teamUserIdsString, $room){
		$result = true;
		$userIdsArr = explode(",", $teamUserIdsString);
		foreach ($userIdsArr as $userId){
			$haveUser = false;
			
			foreach ($dbRoom->originalRoomUsers as $key => $value){
				if($value->profile->userId == $userId){
					$haveUser = true;
					break;
				}
			}
			
			if($haveUser == false){
				$result = false;
				break;
			}
		}
		
		return $result;
	}
	
	
	function checkCanUpdate($firebase, $toCheckRoom){
		$updated = json_decode($firebase->get('updatedScores/'.$toCheckRoom->id.'/'.$toCheckRoom->roundCounter));
		if($updated == 1){
			return false;
		}
		else{
			$firebase->set('updatedScores/'.$toCheckRoom->id.'/'.$toCheckRoom->roundCounter, 1);
			return true;
		}
	}
	
	
	function handleWinners($firebase, $winners, $room){
		foreach ($winners as $teamUserIdsString => $scoreDetailsArr) {
			
			$userIdArr = explode(",", $teamUserIdsString);
			
			$originalScore = json_decode($firebase->get('leaderboard/'.$room->game->abbr.'/'.$teamUserIdsString.'/score'));
			if(empty($originalScore)) $originalScore = 0;
			
			$originalStreak = json_decode($firebase->get('streaks/'.$room->game->abbr.'/'.$teamUserIdsString.'/streakCount'));
			if(empty($originalStreak)) $originalStreak = 0;
			
			$addedScores = 0;
			foreach ($scoreDetailsArr as $scoreDetails){
				if($scoreDetails->addOrMultiply){
					$addedScores = $addedScores + $scoreDetails->value;
				}
			} 
			
			$newScore = 0;
			if($room->game->leaderboardType == "Normal"){
				if($addedScores > $originalScore){
					$newScore = $addedScores;
				}
				else{
					$newScore = $originalScore;
				}
			}
			else if($room->game->leaderboardType == "Accumulate"){
				$newScore = $originalScore + $addedScores;
			}
			
			if($newScore > 0 && $newScore != $originalScore){
				$scoresMap = array(
		   			"score" => $newScore,
				    "userIds" => $userIdArr,
				    ".priority" => $newScore
				);
				$firebase->set('leaderboard/'.$room->game->abbr.'/'.$teamUserIdsString, $scoresMap);
				
				// $priorityMap = array(
					// ".priority" => $newScore
				// );
				// $firebase->set('leaderboard/'.$room->game->abbr.'/'.$teamUserIdsString.'.json', $priorityMap);
// 				
				
				foreach($userIdArr as $userId){
					$firebase->set('userLeaderboardLog/'.$room->game->abbr.'/'.$userId.'/'.$teamUserIdsString, $newScore);
				}
				
			}
			

			$toAddStreak = 0;	
			foreach ($scoreDetailsArr as $scoreDetails){
				if($scoreDetails->canAddStreak){
					$toAddStreak++;
				}
			} 
			
			if($toAddStreak > 0){
				$firebase->set('streaks/'.$room->game->abbr.'/'.$teamUserIdsString.'/streakCount', $originalStreak + $toAddStreak);
			}
		}
	}

	function handleLosers($firebase, $losers, $room){
		if(sizeof($losers) > 0){
			foreach($losers as $teamUserIdsString){
				$originalStreakJson = json_decode($firebase->get('streaks/'.$room->game->abbr.'/'.$teamUserIdsString.'/streakCount'));
				if(!empty($originalStreakJson)){
					$originalStreakCount = json_decode($originalStreakJson);
					
					$streakMap = array(
		   				"streakCount" => 0,
					    "beforeStreakCount" => $originalStreakCount
					);
					
					$firebase->set('streaks/'.$room->game->abbr.'/'.$teamUserIdsString, $streakMap);
				}
			}
		}
		
	}
	
	$winnersJson = $_POST["winnersJson"];
	$losersJson = $_POST["losersJson"];
	$roomJson = $_POST["roomJson"];
	
	$userId = $_POST["userId"];
	$userToken = $_POST["userToken"];
	$room = json_decode($roomJson);
	$winners = json_decode($winnersJson);
	$losers = json_decode($losersJson);
	$firebase = new \Firebase\FirebaseLib($DEFAULT_URL, $DEFAULT_TOKEN);
	
	
	if(checkCanUpdate($firebase, $room)){
		if(checkRoomAndUserValid($firebase, $room, $userId, $userToken)){
			handleLosers($firebase, $losers, $room);
			handleWinners($firebase, $winners, $room);
		}
		else{
			echo "success";
		}
	}
	else{
		echo "success";
	}
?>