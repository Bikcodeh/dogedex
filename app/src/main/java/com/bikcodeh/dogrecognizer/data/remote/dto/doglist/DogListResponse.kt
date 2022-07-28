package com.bikcodeh.dogrecognizer.data.remote.dto.doglist

import com.bikcodeh.dogrecognizer.domain.model.Dog

data class DogListResponse(val dogs: List<Dog>)
