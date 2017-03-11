package com.ntuaece.nikosapos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ntuaece.nikosapos.entities.NodeEntity;
import com.ntuaece.nikosapos.registerpacket.RegisterPacket;

@RestController
public class IcasController {

	@RequestMapping(method = RequestMethod.POST , value = "/register")
	public ResponseEntity registerNode( @RequestBody RegisterPacket registerPacket ){
		if (registerPacket.getId() != -1){
			NodeEntity nodeEntity = new NodeEntity.Builder()
					.setId(registerPacket.getId())
					.setX(registerPacket.getX())
					.setY(registerPacket.getY())
					.setTokens(SimulationParameters.CREDITS_INITIAL)
					.setNodeConnectivityRatio(1.0f)
					.build();
			NodeEntity.NodeEntityList.add(nodeEntity);
			return new ResponseEntity(HttpStatus.CREATED);
		} else {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}
}
